package com.loukwn.gifsoundit.opengs.viewmodel

import com.loukwn.gifsoundit.domain.Event
import com.loukwn.gifsoundit.opengs.Action
import com.loukwn.gifsoundit.opengs.GifState
import com.loukwn.gifsoundit.opengs.PlaybackAction
import com.loukwn.gifsoundit.opengs.SoundState
import com.loukwn.gifsoundit.opengs.State
import com.loukwn.gifsoundit.opengs.UserAction
import javax.inject.Inject

internal class OpenGSStateReducer @Inject constructor() {
    fun reduce(state: State, action: Action): State {
        return when (action) {
            is Action.GifStateChanged -> {
                when (action.gifState) {
                    GifState.GIF_INVALID,
                    GifState.GIF_ERROR,
                    GifState.GIF_LOADING -> state.copy(gifState = action.gifState)
                    GifState.GIF_OK -> {
                        var soundAction = state.soundAction
                        if (state.gifState != GifState.GIF_OK &&
                            state.soundState == SoundState.SOUND_OK
                        ) {
                            soundAction = Event(PlaybackAction.PLAY)
                        }

                        state.copy(gifState = action.gifState, soundAction = soundAction)
                    }
                }
            }
            is Action.SoundStateChanged -> {
                when (action.soundState) {
                    SoundState.SOUND_INVALID,
                    SoundState.SOUND_ERROR,
                    SoundState.SOUND_LOADING -> state.copy(soundState = action.soundState)
                    SoundState.SOUND_OK -> {
                        var soundAction = state.soundAction
                        if (state.soundState != SoundState.SOUND_OK &&
                            state.gifState == GifState.GIF_OK
                        ) {
                            soundAction = Event(PlaybackAction.PLAY)
                        }

                        state.copy(soundState = action.soundState, soundAction = soundAction)
                    }
                    SoundState.SOUND_STARTED -> {
                        state.copy(
                            soundState = action.soundState,
                            gifAction = Event(PlaybackAction.PLAY)
                        )
                    }
                }
            }
            is Action.OnUserAction -> {
                when (action.userAction) {
                    UserAction.ON_PLAYGIFLABEL -> {
                        state.copy(gifAction = Event(PlaybackAction.PLAY))
                    }
                    UserAction.ON_OFFSET_INCREASE -> {
                        state.copy(
                            currentSecondsOffset = state.currentSecondsOffset + 1,
                            soundAction = Event(PlaybackAction.RESTART)
                        )
                    }
                    UserAction.ON_OFFSET_DECREASE -> {
                        state.copy(
                            currentSecondsOffset = (state.currentSecondsOffset - 1).coerceAtLeast(0),
                            soundAction = Event(PlaybackAction.RESTART)
                        )
                    }
                    UserAction.ON_OFFSET_RESET -> {
                        state.copy(
                            currentSecondsOffset = state.soundSource.defaultSecondsOffset,
                            soundAction = Event(PlaybackAction.RESTART)
                        )
                    }
                    UserAction.ON_REFRESH -> {
                        state.copy(
                            currentSecondsOffset = state.currentSecondsOffset,
                            soundAction = Event(PlaybackAction.RESTART)
                        )
                    }
                }
            }
            Action.FragmentCreated -> state.copy(
                gifAction = Event(PlaybackAction.PREPARE),
                soundAction = Event(PlaybackAction.PREPARE)
            )
        }
    }
}
