package com.kostaslou.gifsoundit.opengs.viewmodel

import com.kostaslou.gifsoundit.opengs.GifState
import com.kostaslou.gifsoundit.opengs.GifType
import com.kostaslou.gifsoundit.opengs.OpenGSContract
import com.kostaslou.gifsoundit.opengs.PlaybackAction
import com.kostaslou.gifsoundit.opengs.SoundState
import com.kostaslou.gifsoundit.opengs.State
import javax.inject.Inject

internal class OpenGSViewPresenter @Inject constructor() {
    fun updateView(view: OpenGSContract.View, state: State) {
        state.gifAction.getContentIfNotHandled()?.let { gifAction ->
            val gifType = state.gifSource.gifType
            val gifUrl = state.gifSource.gifUrl

            when (gifAction) {
                PlaybackAction.PREPARE -> {
                    when {
                        gifType == GifType.GIF && gifUrl != null -> {
                            view.prepareGifImage(gifImageUrl = gifUrl)
                        }
                        gifType == GifType.MP4 && gifUrl != null -> {
                            view.prepareGifVideo(gifVideoUrl = gifUrl)
                        }
                        gifType == GifType.YOUTUBE -> view.showYoutubeErrorScreen()
                        else -> {
                            throw IllegalStateException(
                                "Illegal prepare state for Gif Image: $gifUrl, $gifType"
                            )
                        }
                    }
                }
                PlaybackAction.PLAY, PlaybackAction.RESTART -> {
                    when (gifType) {
                        GifType.GIF -> view.startGifImageFromTheStart()
                        GifType.MP4 -> view.startGifVideoFromTheStart()
                        GifType.YOUTUBE -> {
                        }
                    }
                }
            }
        }

        state.soundAction.getContentIfNotHandled()?.let { soundAction ->
            val soundUrl = state.soundSource.soundUrl
            val defaultSecondsOffset = state.soundSource.defaultSecondsOffset.toFloat()
            when (soundAction) {
                PlaybackAction.PREPARE -> {
                    if (soundUrl != null && state.gifSource.gifType != GifType.YOUTUBE) {
                        view.prepareSoundYoutubeView(
                            soundUrl = soundUrl,
                            startSeconds = defaultSecondsOffset
                        )
                    }
                }
                PlaybackAction.PLAY -> view.startSound()
                PlaybackAction.RESTART -> view.seekAndRestartSound(
                    seekSeconds = state.currentSecondsOffset
                )
            }
        }


        if (state.gifState == GifState.GIF_OK && state.soundState == SoundState.SOUND_STARTED) {
            view.setVideoOffsetControlsEnabled(true)
            view.showRefreshButton()
            view.showOffsetSeconds(seconds = state.currentSecondsOffset)
        } else {
            view.setVideoOffsetControlsEnabled(false)
        }

        if (state.gifState == GifState.GIF_OK &&
            (state.soundState == SoundState.SOUND_ERROR ||
                state.soundState == SoundState.SOUND_INVALID)
        ) {
            view.setShowGIFLayoutVisibitity(true)
        } else {
            view.setShowGIFLayoutVisibitity(false)
        }

        updateStatusLabel(view = view, gifState = state.gifState, soundState = state.soundState)
    }

    private fun updateStatusLabel(
        view: OpenGSContract.View,
        gifState: GifState,
        soundState: SoundState
    ) {
        val gifText = when (gifState) {
            GifState.GIF_INVALID -> "invalid"
            GifState.GIF_ERROR -> "errored"
            GifState.GIF_LOADING -> "loading"
            GifState.GIF_OK -> "ready"
        }

        val soundText = when (soundState) {
            SoundState.SOUND_INVALID -> "invalid"
            SoundState.SOUND_ERROR -> "errored"
            SoundState.SOUND_LOADING -> "loading"
            SoundState.SOUND_OK,
            SoundState.SOUND_ENDED,
            SoundState.SOUND_STARTED -> "ready"
        }

        view.setStatusMessage("Gif is $gifText and Sound is $soundText.")
    }
}
