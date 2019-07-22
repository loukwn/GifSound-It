package com.kostaslou.gifsoundit.ui.open

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.google.android.youtube.player.YouTubePlayer
import timber.log.Timber
import java.net.URLDecoder

data class GifLinkState(val gifLink: String?,
                        val gifIsMp4: Boolean?)

// regular viewmodel since youtube + mp4view make it a pain in the ass to retain state

class OpenGSViewModel {

    // local vars
    private var gifLink : String? = null
    private var soundLink: String? = null
    private var seconds = 0
    private var defaultSeconds = 0
    private var gifIsMP4 : Boolean? = null
    private lateinit var query : String

    // sound player
    private var youTubePlayer: YouTubePlayer? = null

    // state
    private var gifSoundPlaybackState: GifSoundPlaybackState = GifSoundPlaybackState()

    // livedata
    val secondOffsetLiveData: MutableLiveData<Int> = MutableLiveData()
    val shareIntentLiveData: MutableLiveData<Intent> = MutableLiveData()
    val gifLinkStateLiveData: MutableLiveData<GifLinkState> = MutableLiveData()
    val soundStateLiveData: MutableLiveData<String?> = MutableLiveData()
    val gifSoundStateLiveData: MutableLiveData<GifSoundPlaybackState> = MutableLiveData()
    val startGifLiveData: MutableLiveData<Boolean> = MutableLiveData()


    init {
        gifSoundStateLiveData.value = gifSoundPlaybackState
    }

    fun setYoutubePlayer(player: YouTubePlayer?) {
        youTubePlayer = player
        youTubePlayer?.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL)
        youTubePlayer?.setPlaybackEventListener(object : YouTubePlayer.PlaybackEventListener {
            override fun onSeekTo(p0: Int) {}
            override fun onBuffering(p0: Boolean) {}
            override fun onPlaying() {
                if (gifSoundPlaybackState.gifState != GifSoundPlaybackState.GifState.GIF_ERROR) {
                    startGifLiveData.value = true
                }
            }
            override fun onStopped() {}
            override fun onPaused() {}

        })
        youTubePlayer?.setPlayerStateChangeListener(object : YouTubePlayer.PlayerStateChangeListener {
            override fun onAdStarted() {}
            override fun onLoading() {}
            override fun onVideoStarted() {}
            override fun onLoaded(p0: String?) {
                changeState(soundState = GifSoundPlaybackState.SoundState.SOUND_OK)
            }
            override fun onError(p0: YouTubePlayer.ErrorReason?) {
                changeState(soundState = GifSoundPlaybackState.SoundState.SOUND_ERROR)
            }
            override fun onVideoEnded() {
                youTubePlayer?.play()
            }
        })
        youTubePlayer?.cueVideo(soundLink, seconds * 1000)
    }

    @Synchronized
    private fun changeState(gifState: GifSoundPlaybackState.GifState? = null, soundState: GifSoundPlaybackState.SoundState? = null, errorMessage: String? = null) {
        // if one of them is null it means that we do not change it

        soundState?.let {
            gifSoundPlaybackState.soundState = it
        }

        gifState?.let {
            gifSoundPlaybackState.gifState = it
        }

        gifSoundPlaybackState.errorMessage = errorMessage
        gifSoundStateLiveData.value = gifSoundPlaybackState
    }

    fun setSoundError() {
        changeState(soundState = GifSoundPlaybackState.SoundState.SOUND_ERROR)
    }

    fun setGifOK() {
         changeState(gifState = GifSoundPlaybackState.GifState.GIF_OK)
    }

    fun setGifError(message: String? = null) {
        changeState(gifState = GifSoundPlaybackState.GifState.GIF_ERROR, errorMessage = message)
    }

    fun setGifSoundArgs(query: String) {
        // save the query so we can share it if we want
        this.query = query
        Timber.d(query)

        // loop query args and save them
        val args = query.split("&")
        for (arg in args) {

            // youtube
            if (arg.startsWith("v=")) {
                soundLink = arg.split("=")[1]
                continue
            }

            // youtube #2
            if (arg.startsWith("sound=")) {
                soundLink = URLDecoder.decode(arg, "UTF-8")

                val temp = soundLink ?: return
                soundLink = URLDecoder.decode(temp.split("=")[2], "UTF-8")

                continue
            }

            // second offset
            if (arg.startsWith("s=")) {
                seconds = arg.split("=")[1].toInt()
                defaultSeconds = seconds
                secondOffsetLiveData.value = defaultSeconds
                continue
            }

            // second offset #2
            if (arg.startsWith("start=")) {
                seconds = arg.split("=")[1].toInt()
                defaultSeconds = seconds
                secondOffsetLiveData.value = defaultSeconds
                continue
            }

            // imgur gif #1
            if (arg.startsWith("gifv=")) {
                gifLink = "http://i.imgur.com/" + arg.split("=")[1] + ".mp4"
                gifIsMP4 = true
                continue
            }

            // imgur gif #2
            if (arg.startsWith("mp4")) {
                gifLink = arg.split("=")[1] + ".mp4"
                gifIsMP4 = true
                continue
            }

            // gfycat gif
            if (arg.startsWith("gfycat")) {
                gifLink = "https://giant.gfycat.com/" + arg.split("=")[1] + ".mp4"
                gifIsMP4 = true
                continue
            }

            // normal gif
            if (arg.startsWith("gif=")) {
                gifLink = URLDecoder.decode(arg.split("=")[1], "UTF-8")

                val temp = gifLink ?: return
                if (!temp.startsWith("http") && !temp.startsWith("https"))
                    gifLink = "http://$temp"

                gifIsMP4 = false
                continue
            }

            // webm
            if (arg.startsWith("webm=")) {
                gifLink = URLDecoder.decode(arg.split("=")[1], "UTF-8") + ".webm"

                val temp = gifLink ?: return
                if (!temp.startsWith("http") && !temp.startsWith("https"))
                    gifLink = "http://$temp"

                gifIsMP4 = true
                continue
            }
        }

        // inform view, so it can start inits
        soundStateLiveData.value = soundLink
        gifLinkStateLiveData.value = GifLinkState(gifLink, gifIsMP4)


        // if any of them is null, the view should update the status
        if (soundLink == null)
            changeState(soundState = GifSoundPlaybackState.SoundState.SOUND_INVALID)

        if (gifLink == null)
            changeState(gifState = GifSoundPlaybackState.GifState.GIF_INVALID)
    }

    fun restartSound() {
        youTubePlayer?.pause()
        youTubePlayer?.seekToMillis(seconds * 1000)
        youTubePlayer?.play()
    }

    fun startGifSound() {
        youTubePlayer?.play()
    }


    // button listeners
    fun shareButtonClicked() {
        if (!query.startsWith("?"))
            query = "?$query"

        val textToSend = "https://gifsound.com/$query"

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textToSend)
            type = "text/plain"
        }

        shareIntentLiveData.value = sendIntent
    }

    fun revertButtonClicked() {
        seconds = defaultSeconds
        secondOffsetLiveData.value = seconds
    }

    fun addButtonClicked() {
        seconds += 1
        secondOffsetLiveData.value = seconds
    }

    fun decreaseButtonClicked() {
        seconds = if (seconds>0) seconds -1 else 0
        secondOffsetLiveData.value = seconds
    }
}