package com.kostaslou.gifsoundit.opengs.controller

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gifsoundit.opengs.R
import com.kostaslou.gifsoundit.opengs.mappers.QueryToUIModelMapper
import com.kostaslou.gifsoundit.opengs.view.OpenGSViewMvc
import com.kostaslou.gifsoundit.opengs.view.OpenGSViewMvcImpl
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import timber.log.Timber

class OpenGSFragment : Fragment(), OpenGSViewMvc.Listener {

    // view
    private var viewMvc: OpenGSViewMvc? = null
    private lateinit var uiModel: OpenGSUIModel

    // state
    private val query by lazy { arguments?.getString(PARAM_QUERY) ?: "" }

    // youtube listeners
    private val gifYouTubePlayerListener = object : AbstractYouTubePlayerListener() {
        override fun onReady(youTubePlayer: YouTubePlayer) {
            super.onReady(youTubePlayer)
            setYoutubePlayer(youTubePlayer, true)
            updateUIModel(gifState = GifState.GIF_OK)
        }

        override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
            super.onError(youTubePlayer, error)
            updateUIModel(gifState = GifState.GIF_ERROR)
        }
    }
    private val soundYouTubePlayerListener = object : AbstractYouTubePlayerListener() {
        override fun onReady(youTubePlayer: YouTubePlayer) {
            super.onReady(youTubePlayer)
            setYoutubePlayer(youTubePlayer, false)
            updateUIModel(soundState = SoundState.SOUND_OK)
        }

        override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
            super.onError(youTubePlayer, error)
            updateUIModel(soundState = SoundState.SOUND_ERROR)
        }
    }

    // youtube players
    private var soundYoutubePlayer: YouTubePlayer? = null
    private var gifYoutubePlayer: YouTubePlayer? = null

    //
    // Android lifecycle
    //

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewMvc = OpenGSViewMvcImpl(requireContext(), inflater, container)
        return viewMvc?.getRootView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Parse query and setup view
        Timber.d("GifSound Url: $query")
        uiModel = QueryToUIModelMapper().getUIModel(query)
        viewMvc?.initView(uiModel, gifYouTubePlayerListener, soundYouTubePlayerListener)

        // YoutubeViews need to be bound to the lifecycle of the Fragment
        if (uiModel.gifSource.gifType == GifSource.GifType.YOUTUBE) {
            lifecycle.addObserver(viewMvc?.getYoutubeGifView()!!)
        }
        lifecycle.addObserver(viewMvc?.getSoundGifView()!!)
    }

    override fun onStart() {
        super.onStart()
        viewMvc?.setListener(this)
    }

    override fun onStop() {
        super.onStop()
        viewMvc?.removeListener(this)
    }

    override fun onDestroy() {
        viewMvc = null
        super.onDestroy()
    }

    //
    // YouTube stuff
    //

    private fun restartSound() {
        soundYoutubePlayer?.pause()
        soundYoutubePlayer?.seekTo(uiModel.secondsVideoDefaultOffset.toFloat())
        soundYoutubePlayer?.play()
    }

    private fun startGifSound() {
        soundYoutubePlayer?.play()
    }

    private fun startYoutubeGifFromTheStart() {
        gifYoutubePlayer?.seekTo(0f)
        gifYoutubePlayer?.play()
    }

    @Synchronized
    fun setYoutubePlayer(player: YouTubePlayer?, forGif: Boolean) {
        val youtubeListener = object : AbstractYouTubePlayerListener() {
            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                super.onStateChange(youTubePlayer, state)

                when (state) {
                    PlayerConstants.PlayerState.VIDEO_CUED -> {
                        if (forGif)
                            updateUIModel(gifState = GifState.GIF_OK)
                        else
                            updateUIModel(soundState = SoundState.SOUND_OK)
                    }
                    PlayerConstants.PlayerState.ENDED -> {
                        if (forGif) {
                            youTubePlayer.seekTo(0f)
                            youTubePlayer.play()
                        } else {
                            youTubePlayer.play()
                        }
                    }
                    PlayerConstants.PlayerState.PLAYING -> {
                        if (!forGif) {
                            if (uiModel.gifState != GifState.GIF_ERROR) {
                                viewMvc?.startGifFromTheStart(uiModel)
                                if (uiModel.gifSource.gifType == GifSource.GifType.YOUTUBE) {
                                    startYoutubeGifFromTheStart()
                                }
                            }
                        }
                    }
                    else -> {
                    }
                }
            }

            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                super.onError(youTubePlayer, error)
                updateUIModel(soundState = SoundState.SOUND_ERROR)
            }
        }

        if (forGif) {
            gifYoutubePlayer = player
            gifYoutubePlayer?.addListener(youtubeListener)
            gifYoutubePlayer?.cueVideo(uiModel.gifSource.gifLink ?: "", 0f)
        } else {
            soundYoutubePlayer = player
            soundYoutubePlayer?.addListener(youtubeListener)
            soundYoutubePlayer?.cueVideo(
                uiModel.soundSource.soundPart ?: "",
                uiModel.secondsVideoDefaultOffset.toFloat()
            )
        }
    }

    //
    // Communicate with View
    //

    // if gif is ok and sound is either error or invalid, give the option to play the first anyway.
    private fun setShowGifLabelStatusIfNeeded() {
        if (uiModel.gifState == GifState.GIF_OK &&
            (uiModel.soundState == SoundState.SOUND_ERROR ||
                uiModel.soundState == SoundState.SOUND_INVALID)
        ) {
            viewMvc?.showGIFPlayLayout()
        }
    }

    private fun refreshGifSound(refreshOnlyGif: Boolean = false) {

        // restart sound
        if (!refreshOnlyGif) {
            restartSound()
        }

        // restart gif
        viewMvc?.startGifFromTheStart(uiModel)
        if (uiModel.gifSource.gifType == GifSource.GifType.YOUTUBE) {
            startYoutubeGifFromTheStart()
        }
    }

    @Synchronized
    private fun updateUIModel(
        gifSource: GifSource = uiModel.gifSource,
        soundSource: SoundSource = uiModel.soundSource,
        secondsVideoDefaultOffset: Int = uiModel.secondsVideoDefaultOffset,
        secondsVideoOffset: Int = uiModel.secondsVideoOffset,
        gifState: GifState = uiModel.gifState,
        soundState: SoundState = uiModel.soundState,
        errorMessage: String? = uiModel.errorMessage
    ) {
        uiModel = uiModel.copy(
            gifSource = gifSource,
            soundSource = soundSource,
            secondsVideoDefaultOffset = secondsVideoDefaultOffset,
            secondsVideoOffset = secondsVideoOffset,
            gifState = gifState,
            soundState = soundState,
            errorMessage = errorMessage
        )

        if (gifState == GifState.GIF_OK && soundState == SoundState.SOUND_OK) {
            startGifSound()
        }
        setShowGifLabelStatusIfNeeded()
        viewMvc?.updateStatusMessages(uiModel)
    }

    //
    // View event listeners
    //

    override fun onBackButtonPressed() {
        (activity as? Callback)?.onOpenGSBackPressed()
    }

    override fun onRefreshButtonPressed() {
        refreshGifSound()
    }

    override fun onShareButtonPressed() {
        val textToSend = if (query.startsWith("?"))
            "https://gifsound.com/$query"
        else
            "https://gifsound.com/?$query"

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textToSend)
            type = "text/plain"
        }

        startActivity(Intent.createChooser(sendIntent, resources.getText(R.string.opengs_send_to)))
    }

    override fun onPlayGifLabelPressed() {
        refreshGifSound(refreshOnlyGif = true)
    }

    override fun onOffsetIncreaseButtonPressed() {
        updateUIModel(secondsVideoOffset = uiModel.secondsVideoOffset + 1)
        refreshGifSound()
    }

    override fun onOffsetDecreaseButtonPressed() {
        val seconds = if (uiModel.secondsVideoOffset > 0) uiModel.secondsVideoOffset - 1 else 0
        updateUIModel(secondsVideoOffset = seconds)
        refreshGifSound()
    }

    override fun onOffsetResetButtonPressed() {
        updateUIModel(secondsVideoOffset = uiModel.secondsVideoDefaultOffset)
        refreshGifSound()
    }

    override fun setGifState(gifState: GifState) {
        updateUIModel(gifState = gifState)
    }

    override fun setSoundState(soundState: SoundState) {
        updateUIModel(soundState = soundState)
    }

    interface Callback {
        fun onOpenGSBackPressed()
    }

    companion object {
        const val PARAM_QUERY = "QUERY"
    }
}
