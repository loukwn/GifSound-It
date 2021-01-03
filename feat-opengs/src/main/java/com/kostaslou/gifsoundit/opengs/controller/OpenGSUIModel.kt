package com.kostaslou.gifsoundit.opengs.controller

data class OpenGSUIModel(
    val gifSource: GifSource,
    val soundSource: SoundSource,
    val secondsVideoDefaultOffset: Int,
    val secondsVideoOffset: Int,
    val gifState: GifState,
    val soundState: SoundState,
    val errorMessage: String?
)

// Represents the gif part of the initial url
data class GifSource(
    var gifLink: String?,
    var gifType: GifType = GifType.GIF
) {
    enum class GifType {
        GIF, MP4
    }
}

// Represents the sound part of the initial url
data class SoundSource(var soundPart: String?)

// State of Gif (video) playback
enum class GifState {
    GIF_INVALID, GIF_ERROR, GIF_LOADING, GIF_OK
}

// State of Sound playback
enum class SoundState {
    SOUND_INVALID, SOUND_ERROR, SOUND_LOADING, SOUND_OK
}
