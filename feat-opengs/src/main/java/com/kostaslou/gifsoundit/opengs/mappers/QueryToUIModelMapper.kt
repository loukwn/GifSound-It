package com.kostaslou.gifsoundit.opengs.mappers

import com.kostaslou.gifsoundit.opengs.controller.GifSource
import com.kostaslou.gifsoundit.opengs.controller.GifSource.GifType
import com.kostaslou.gifsoundit.opengs.controller.GifState
import com.kostaslou.gifsoundit.opengs.controller.OpenGSUIModel
import com.kostaslou.gifsoundit.opengs.controller.SoundSource
import com.kostaslou.gifsoundit.opengs.controller.SoundState
import timber.log.Timber
import java.net.URLDecoder

/**
 * Maps the input query url to the data ready for displaying to the UI (UI model)
 */
class QueryToUIModelMapper {

    fun getUIModel(query: String): OpenGSUIModel {
        var soundLink: String? = null
        var gifLink: String? = null
        var gifType: GifType = GifType.GIF
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

            // youtube
            if (arg.startsWith("v=")) {
                soundLink = arg.split("=")[1]
                continue
            }

            // youtube #2
            if (arg.startsWith("sound=")) {
                soundLink = URLDecoder.decode(arg, "UTF-8")

                soundLink?.let {
                    soundLink = URLDecoder.decode(it.split("=")[2], "UTF-8")
                }

                continue
            }

            // second offset
            if (arg.startsWith("s=")) {
                seconds = arg.split("=")[1].toInt()
                continue
            }

            // second offset #2
            if (arg.startsWith("start=")) {
                seconds = arg.split("=")[1].toInt()
                continue
            }

            // imgur gif #1
            if (arg.startsWith("gifv=")) {
                gifLink = "https://i.imgur.com/" + arg.split("=")[1] + ".mp4"
                gifType = GifType.MP4
                continue
            }

            // imgur gif #2
            if (arg.startsWith("mp4")) {
                gifLink = URLDecoder.decode(arg.split("=")[1] + ".mp4", "UTF-8")
                gifType = GifType.MP4
                continue
            }

            // gfycat gif
            if (arg.startsWith("gfycat")) {
                gifLink = "https://giant.gfycat.com/" + arg.split("=")[1] + ".mp4"
                gifType = GifType.MP4
                continue
            }

            // normal gif
            if (arg.startsWith("gif=")) {
                gifLink = URLDecoder.decode(arg.split("=")[1], "UTF-8")

                gifLink?.let {

                    if (it.contains("youtu")) {
                        throw IllegalStateException("Youtube as gif not supported")
                    } else {
                        gifLink = if (it.startsWith("http://")) {
                            "https://${it.removePrefix("http://")}"
                        } else if (!it.startsWith("https"))
                            "https://$it"
                        else {
                            it
                        }
                    }
                }

                continue
            }

            // webm
            if (arg.startsWith("webm=")) {
                gifLink = URLDecoder.decode(arg.split("=")[1], "UTF-8") + ".webm"

                gifLink?.let {
                    if (!it.startsWith("http") && !it.startsWith("https"))
                        gifLink = "https://$it"

                    gifType = GifType.MP4
                }

                continue
            }
        }

        val gifState = if (gifLink == null) GifState.GIF_INVALID else GifState.GIF_LOADING
        val soundState =
            if (soundLink == null) SoundState.SOUND_INVALID else SoundState.SOUND_LOADING

        Timber.d("Parsed Gif Link: $gifLink")
        Timber.d("Parsed Sound Link: $soundLink")

        return OpenGSUIModel(
            gifSource = GifSource(gifLink, gifType),
            soundSource = SoundSource(soundLink),
            secondsVideoDefaultOffset = seconds,
            secondsVideoOffset = seconds,
            gifState = gifState,
            soundState = soundState,
            errorMessage = null
        )
    }
}
