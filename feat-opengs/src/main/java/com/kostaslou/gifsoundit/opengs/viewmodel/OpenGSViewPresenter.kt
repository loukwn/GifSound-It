package com.kostaslou.gifsoundit.opengs.viewmodel

import android.content.res.Resources
import com.kostaslou.gifsoundit.opengs.GifState
import com.kostaslou.gifsoundit.opengs.GifType
import com.kostaslou.gifsoundit.opengs.OpenGSContract
import com.kostaslou.gifsoundit.opengs.PlaybackAction
import com.kostaslou.gifsoundit.opengs.SoundState
import com.kostaslou.gifsoundit.opengs.State
import com.kostaslou.opengs.R
import javax.inject.Inject

internal class OpenGSViewPresenter @Inject constructor(
    private val resources: Resources,
) {
    fun updateView(view: OpenGSContract.View, state: State) {
        handleGifEvent(state, view)
        handleSoundEvent(state, view)
        handleUiControlsState(state, view)
        handleGifErrorVisibility(state, view)
        handlePlayGifLabelVisibility(state, view)
        updateStatusLabel(view = view, gifState = state.gifState, soundState = state.soundState)
    }

    private fun handleUiControlsState(state: State, view: OpenGSContract.View) {
        if (state.gifState == GifState.GIF_OK && state.soundState == SoundState.SOUND_STARTED) {
            view.setVideoOffsetControlsEnabled(true)
            view.showRefreshButton()
            view.showOffsetSeconds(seconds = state.currentSecondsOffset)
        } else {
            view.setVideoOffsetControlsEnabled(false)
        }
    }

    private fun handleGifErrorVisibility(state: State, view: OpenGSContract.View) {
        if (state.gifState == GifState.GIF_ERROR) view.showGifErrorScreen()
    }

    private fun handlePlayGifLabelVisibility(state: State, view: OpenGSContract.View) {
        val showGifLayout = state.gifState == GifState.GIF_OK &&
            (
                state.soundState == SoundState.SOUND_ERROR ||
                    state.soundState == SoundState.SOUND_INVALID
                )

        view.setShowGIFLayoutVisibitity(showGifLayout)
    }

    private fun handleSoundEvent(state: State, view: OpenGSContract.View) {
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
    }

    private fun handleGifEvent(state: State, view: OpenGSContract.View) {
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
    }

    private fun updateStatusLabel(
        view: OpenGSContract.View,
        gifState: GifState,
        soundState: SoundState
    ) {
        val gifText = resources.getString(gifState.errorTextRes)
        val soundText = resources.getString(soundState.errorTextRes)
        val statusMessage = resources.getString(R.string.opengs_status_message, gifText, soundText)

        view.setStatusMessage(statusMessage)
    }
}
