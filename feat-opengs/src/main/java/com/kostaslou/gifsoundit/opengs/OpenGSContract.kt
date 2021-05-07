package com.kostaslou.gifsoundit.opengs

import com.kostaslou.gifsoundit.common.contract.ActionableViewContract
import com.kostaslou.gifsoundit.common.util.Event
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

internal interface OpenGSContract {

    interface View : ActionableViewContract<Listener> {
        fun getSoundYoutubePlayerView(): YouTubePlayerView
        fun prepareGifImage(gifImageUrl: String)
        fun prepareGifVideo(gifVideoUrl: String)
        fun prepareSoundYoutubeView(soundUrl: String, startSeconds: Float)
        fun startSound()
        fun startGifImageFromTheStart()
        fun startGifVideoFromTheStart()
        fun seekAndRestartSound(seekSeconds: Int)
        fun showYoutubeErrorScreen()
        fun setStatusMessage(message: String)
        fun setVideoOffsetControlsEnabled(enabled: Boolean)
        fun showRefreshButton()
        fun showOffsetSeconds(seconds: Int)
        fun setShowGIFLayoutVisibitity(visible: Boolean)
        fun release()
    }

    interface Listener {
        fun onBackButtonPressed()
        fun onRefreshButtonPressed()
        fun onShareButtonPressed()
        fun onPlayGifLabelPressed()
        fun onOffsetIncreaseButtonPressed()
        fun onOffsetDecreaseButtonPressed()
        fun onOffsetResetButtonPressed()
        fun onGifStateChanged(gifState: GifState)
        fun onSoundStateChanged(soundState: SoundState)
        fun onViewWebsiteButtonPressed()
    }

    interface ViewModel {
        fun setView(view: View)
        fun setup(query: String, isFromDeepLink: Boolean)
    }
}

internal sealed class Action {
    data class GifStateChanged(val gifState: GifState): Action()
    data class SoundStateChanged(val soundState: SoundState): Action()
    data class OnUserAction(val userAction: UserAction): Action()
}

internal enum class UserAction {
    ON_REFRESH, ON_PLAYGIFLABEL, ON_OFFSET_INCREASE, ON_OFFSET_DECREASE, ON_OFFSET_RESET
}

internal data class State(
    val gifSource: GifSource,
    val soundSource: SoundSource,
    val gifState: GifState,
    val soundState: SoundState,
    val gifAction: Event<PlaybackAction>,
    val soundAction: Event<PlaybackAction>,
    val currentSecondsOffset: Int,
    val isFromDeepLink: Boolean,
) {
    companion object
}

internal enum class PlaybackAction {
    PREPARE, PLAY, RESTART
}

internal enum class GifState {
    GIF_INVALID, GIF_ERROR, GIF_LOADING, GIF_OK
}

internal enum class SoundState {
    SOUND_INVALID, SOUND_ERROR, SOUND_LOADING, SOUND_OK, SOUND_STARTED
}

internal data class GifSource(val gifUrl: String?, val gifType: GifType)

internal data class SoundSource(val soundUrl: String?, val defaultSecondsOffset: Int)

internal enum class GifType {
    GIF, MP4, YOUTUBE
}
