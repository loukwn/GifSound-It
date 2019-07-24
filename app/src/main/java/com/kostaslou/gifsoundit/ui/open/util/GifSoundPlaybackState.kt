package com.kostaslou.gifsoundit.ui.open.util

// the state of the gifsound that is being played
class GifSoundPlaybackState {

    enum class GifState {
        GIF_INVALID, GIF_ERROR, GIF_LOADING, GIF_OK
    }

    enum class SoundState {
        SOUND_INVALID, SOUND_ERROR, SOUND_LOADING, SOUND_OK
    }

    var gifState = GifState.GIF_LOADING
    var soundState = SoundState.SOUND_LOADING
    var errorMessage: String? = null
}