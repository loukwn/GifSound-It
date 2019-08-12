package com.kostaslou.gifsoundit.opengs

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.kostaslou.gifsoundit.opengs.util.GifSoundPlaybackState
import com.kostaslou.gifsoundit.opengs.util.GifUrl
import com.kostaslou.gifsoundit.opengs.util.GifsoundUrlParser
import com.kostaslou.gifsoundit.opengs.util.SoundUrl
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import timber.log.Timber

// regular viewmodel since youtube + mp4view make it a pain in the ass to retain state

class OpenGSViewModel {

    // local vars
    private var gifUrl = GifUrl(null)
    private var soundUrl = SoundUrl(null)
    private var seconds = 0
    private var defaultSeconds = 0
    private lateinit var query: String

    // sound player
    private var youTubePlayer: YouTubePlayer? = null

    // state
    private var gifSoundPlaybackState: GifSoundPlaybackState = GifSoundPlaybackState()

    // livedata for the view
    val secondOffsetLiveData: MutableLiveData<Int> = MutableLiveData()
    val gifUrlLiveData: MutableLiveData<GifUrl> = MutableLiveData()
    val soundUrlLiveData: MutableLiveData<SoundUrl> = MutableLiveData()
    val gifSoundStateLiveData: MutableLiveData<GifSoundPlaybackState> = MutableLiveData()
    val shareIntentLiveData: MutableLiveData<Intent> = MutableLiveData()
    val startGifLiveData: MutableLiveData<Boolean> = MutableLiveData()

    //
    // constructor
    //

    init {
        gifSoundStateLiveData.value = gifSoundPlaybackState
    }

    //
    // view sends the query to be parsed
    //

    fun setGifSoundArgs(query: String) {
        // save the query so we can share it if we want
        this.query = query
        Timber.d(query)

        // get the gif and sound arguments
        val parser = GifsoundUrlParser(query)
        soundUrl = parser.getSoundUrl()
        gifUrl = parser.getGifUrl()
        seconds = parser.getSeconds()
        defaultSeconds = seconds

        // inform view, so it can start inits
        secondOffsetLiveData.value = defaultSeconds
        soundUrlLiveData.value = soundUrl
        gifUrlLiveData.value = gifUrl

        // if any of them is null, the view should update the status
        if (soundUrl.soundLink == null)
            changeState(soundState = GifSoundPlaybackState.SoundState.SOUND_INVALID)

        if (gifUrl.gifLink == null)
            changeState(gifState = GifSoundPlaybackState.GifState.GIF_INVALID)
    }

    //
    // set youtube player
    //

    fun setYoutubePlayer(player: YouTubePlayer?) {
        youTubePlayer = player

        youTubePlayer?.addListener(object : AbstractYouTubePlayerListener() {
            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                super.onStateChange(youTubePlayer, state)

                when (state) {
                    PlayerConstants.PlayerState.VIDEO_CUED -> {
                        changeState(soundState = GifSoundPlaybackState.SoundState.SOUND_OK)
                    }
                    PlayerConstants.PlayerState.ENDED -> {
                        youTubePlayer.play()
                    }
                    PlayerConstants.PlayerState.PLAYING -> {
                        if (gifSoundPlaybackState.gifState != GifSoundPlaybackState.GifState.GIF_ERROR) {
                            startGifLiveData.value = true
                        }
                    }
                    else -> {}
                }
            }

            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                super.onError(youTubePlayer, error)
                changeState(soundState = GifSoundPlaybackState.SoundState.SOUND_ERROR)
            }
        })

        youTubePlayer?.cueVideo(soundUrl.soundLink?:"", seconds.toFloat())
    }

    //
    // change the gifsoundPlayback state
    //

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

    //
    // view changes the state
    //

    fun setSoundError() {
        changeState(soundState = GifSoundPlaybackState.SoundState.SOUND_ERROR)
    }

    fun setGifOK() {
        changeState(gifState = GifSoundPlaybackState.GifState.GIF_OK)
    }

    fun setGifError(message: String? = null) {
        changeState(gifState = GifSoundPlaybackState.GifState.GIF_ERROR, errorMessage = message)
    }

    fun restartSound() {
        youTubePlayer?.pause()
        youTubePlayer?.seekTo(seconds.toFloat())
        youTubePlayer?.play()
    }

    fun startGifSound() {
        youTubePlayer?.play()
    }

    //
    // a button is clicked in the view
    //

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
        seconds = if (seconds> 0) seconds - 1 else 0
        secondOffsetLiveData.value = seconds
    }
}
