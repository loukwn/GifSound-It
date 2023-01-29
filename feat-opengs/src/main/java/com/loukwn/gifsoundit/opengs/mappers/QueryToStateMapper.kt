package com.loukwn.gifsoundit.opengs.mappers

import com.loukwn.gifsoundit.common.util.Event
import com.loukwn.gifsoundit.opengs.GifSource
import com.loukwn.gifsoundit.opengs.GifState
import com.loukwn.gifsoundit.opengs.GifType
import com.loukwn.gifsoundit.opengs.PlaybackAction
import com.loukwn.gifsoundit.opengs.SoundSource
import com.loukwn.gifsoundit.opengs.SoundState
import com.loukwn.gifsoundit.opengs.State
import timber.log.Timber
import java.net.URLDecoder
import javax.inject.Inject

/**
 * Maps the input query url to the data ready for displaying to the UI (UI model)
 */
internal class QueryToStateMapper @Inject constructor() {

    fun getState(query: String, isFromDeepLink: Boolean): State {
        var soundLink: String? = null
        var gifSource = GifSource(null, GifType.GIF)
        var seconds = 0

        // get the query part of the link
        val partsOfLink = query.split("?")
        val finalQuery = if (partsOfLink.size > 1) {
            var temp = ""
            for (i in 1 until partsOfLink.size)
                temp += "?" + partsOfLink[i]
            temp.substring(1)
        } else {
            null
        }

        val args = finalQuery!!.split("&")
        for (arg in args) {
            when {
                arg.startsWith("v=") -> soundLink = parseArgAsYoutube1(arg)
                arg.startsWith("sound=") -> soundLink = parseArgAsYoutube2(arg)
                arg.startsWith("s=") -> seconds = parseSeconds1(arg)
                arg.startsWith("start=") -> seconds = parseSeconds2(arg)
                arg.startsWith("gifv=") -> gifSource = parseImgurGif1(arg)
                arg.startsWith("mp4=") -> gifSource = parseImgurGif2(arg)
                arg.startsWith("gfycat=") -> gifSource = parseGfycat(arg)
                arg.startsWith("gif=") -> gifSource = parseGif(arg)
                arg.startsWith("webm=") -> gifSource = parseWebm(arg)
            }
        }

        val gifState = when {
            gifSource.gifUrl == null -> GifState.GIF_INVALID
            gifSource.gifType == GifType.YOUTUBE -> GifState.GIF_INVALID
            else -> GifState.GIF_LOADING
        }
        val soundState =
            if (soundLink == null) SoundState.SOUND_INVALID else SoundState.SOUND_LOADING

        Timber.d("Parsed Gif Link: ${gifSource.gifUrl}")
        Timber.d("Parsed Sound Link: $soundLink")

        return State(
            gifSource = gifSource,
            soundSource = SoundSource(soundLink, seconds),
            gifState = gifState,
            soundState = soundState,
            currentSecondsOffset = seconds,
            gifAction = Event(PlaybackAction.PREPARE),
            soundAction = Event(PlaybackAction.PREPARE),
            isFromDeepLink = isFromDeepLink,
        )
    }

    private fun parseArgAsYoutube1(arg: String): String {
        return arg.split("=")[1]
    }

    private fun parseArgAsYoutube2(arg: String): String? {
        return URLDecoder.decode(arg, "UTF-8")?.let {
            URLDecoder.decode(it.split("=")[2], "UTF-8")
        }
    }

    private fun parseSeconds1(arg: String): Int {
        return arg.split("=")[1].toInt()
    }

    private fun parseSeconds2(arg: String): Int {
        return arg.split("=")[1].toInt()
    }

    private fun parseImgurGif1(arg: String): GifSource {
        return GifSource(
            gifUrl = "https://i.imgur.com/" + arg.split("=")[1] + ".mp4",
            gifType = GifType.MP4
        )
    }

    private fun parseImgurGif2(arg: String): GifSource {
        return GifSource(
            gifUrl = URLDecoder.decode(arg.split("=")[1] + ".mp4", "UTF-8"),
            gifType = GifType.MP4
        )
    }

    private fun parseGfycat(arg: String): GifSource {
        return GifSource(
            gifUrl = "https://giant.gfycat.com/" + arg.split("=")[1] + ".mp4",
            gifType = GifType.MP4
        )
    }

    private fun parseGif(arg: String): GifSource {
        var gifLink: String? = null
        var gifType: GifType = GifType.GIF

        URLDecoder.decode(arg.split("=")[1], "UTF-8")?.let {
            when {
                it.startsWith("giant.gfycat") -> {
                    gifLink = "https://${it.substringBefore(".gif")}.mp4"
                    gifType = GifType.MP4
                }

                it.contains("youtu") -> {
                    gifLink = it
                    gifType = GifType.YOUTUBE
                }

                else -> {
                    gifLink = if (it.startsWith("http://")) {
                        "https://${it.removePrefix("http://")}"
                    } else if (!it.startsWith("https"))
                        "https://$it"
                    else {
                        it
                    }
                    gifType = GifType.GIF
                }
            }
        }

        return GifSource(
            gifUrl = gifLink,
            gifType = gifType
        )
    }

    private fun parseWebm(arg: String): GifSource {
        var gifLink: String? = null
        var gifType: GifType = GifType.GIF

        URLDecoder.decode(arg.split("=")[1], "UTF-8")?.let {
            gifLink = "$it.webm"
            if (!it.startsWith("http") && !it.startsWith("https")) {
                gifLink = "https://$gifLink"
            }

            gifType = GifType.MP4
        }

        return GifSource(
            gifUrl = gifLink,
            gifType = gifType
        )
    }
}
