package com.kostaslou.gifsoundit.opengs.util

import java.net.URLDecoder

// represents the gif part of the initial url
data class GifUrl(
    var gifLink: String?,
    var gifType: GifType = GifType.GIF
) {

    enum class GifType {
        GIF, MP4
    }
}

// represents the sound part of the initial url
data class SoundUrl(var soundLink: String?)

// parses the initial url and returns the gif and sound parts + the seconds offset
class GifsoundUrlParser(query: String) {

    private var gifUrl = GifUrl(null)
    private var soundUrl = SoundUrl(null)
    private var seconds = 0

    init {
        val args = query.split("&")
        for (arg in args) {

            // youtube
            if (arg.startsWith("v=")) {
                soundUrl.soundLink = arg.split("=")[1]
                continue
            }

            // youtube #2
            if (arg.startsWith("sound=")) {
                soundUrl.soundLink = URLDecoder.decode(arg, "UTF-8")

                soundUrl.soundLink?.let {
                    soundUrl.soundLink = URLDecoder.decode(it.split("=")[2], "UTF-8")
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
                gifUrl.gifLink = "http://i.imgur.com/" + arg.split("=")[1] + ".mp4"
                gifUrl.gifType = GifUrl.GifType.MP4
                continue
            }

            // imgur gif #2
            if (arg.startsWith("mp4")) {
                gifUrl.gifLink = arg.split("=")[1] + ".mp4"
                gifUrl.gifType = GifUrl.GifType.MP4
                continue
            }

            // gfycat gif
            if (arg.startsWith("gfycat")) {
                gifUrl.gifLink = "https://giant.gfycat.com/" + arg.split("=")[1] + ".mp4"
                gifUrl.gifType = GifUrl.GifType.MP4
                continue
            }

            // normal gif
            if (arg.startsWith("gif=")) {
                gifUrl.gifLink = URLDecoder.decode(arg.split("=")[1], "UTF-8")

                gifUrl.gifLink?.let {
                    if (!it.startsWith("http") && !it.startsWith("https"))
                        gifUrl.gifLink = "http://$it"
                }

                continue
            }

            // webm
            if (arg.startsWith("webm=")) {
                gifUrl.gifLink = URLDecoder.decode(arg.split("=")[1], "UTF-8") + ".webm"

                gifUrl.gifLink?.let {
                    if (!it.startsWith("http") && !it.startsWith("https"))
                        gifUrl.gifLink = "http://$it"

                    gifUrl.gifType = GifUrl.GifType.MP4
                }

                continue
            }
        }
    }

    fun getGifUrl() = gifUrl
    fun getSoundUrl() = soundUrl
    fun getSeconds() = seconds
}
