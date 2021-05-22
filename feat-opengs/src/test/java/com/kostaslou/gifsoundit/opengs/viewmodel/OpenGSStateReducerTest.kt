package com.kostaslou.gifsoundit.opengs.viewmodel

import com.kostaslou.gifsoundit.common.util.Event
import com.kostaslou.gifsoundit.opengs.Action
import com.kostaslou.gifsoundit.opengs.GifState
import com.kostaslou.gifsoundit.opengs.PlaybackAction
import com.kostaslou.gifsoundit.opengs.SoundSource
import com.kostaslou.gifsoundit.opengs.SoundState
import com.kostaslou.gifsoundit.opengs.State
import com.kostaslou.gifsoundit.opengs.UserAction
import com.kostaslou.gifsoundit.opengs.default
import org.junit.Assert.assertEquals
import org.junit.Test

internal class OpenGSStateReducerTest {
    private val sut = OpenGSStateReducer()

    @Test
    fun `GIVEN action is GifStateChanged_GIF_INVALID WHEN map THEN update state`() {
        val action = Action.GifStateChanged(GifState.GIF_INVALID)

        val newState = sut.map(State.default(), action)

        assertEquals(GifState.GIF_INVALID, newState.gifState)
    }

    @Test
    fun `GIVEN action is GifStateChanged_GIF_ERROR WHEN map THEN update state`() {
        val action = Action.GifStateChanged(GifState.GIF_ERROR)

        val newState = sut.map(State.default(), action)

        assertEquals(GifState.GIF_ERROR, newState.gifState)
    }

    @Test
    fun `GIVEN action is GifStateChanged_GIF_OK AND gifState was not OK AND soundState was OK WHEN map THEN start sound`() {
        val action = Action.GifStateChanged(GifState.GIF_OK)
        val state = State.default(gifState = GifState.GIF_LOADING, soundState = SoundState.SOUND_OK)

        val newState = sut.map(state, action)

        assertEquals(Event(PlaybackAction.PLAY), newState.soundAction)
    }

    @Test
    fun `GIVEN action is GifStateChanged_GIF_OK AND soundState was not OK WHEN map THEN keep previous soundAction`() {
        val action = Action.GifStateChanged(GifState.GIF_OK)
        val state = State.default(
            gifState = GifState.GIF_LOADING,
            soundState = SoundState.SOUND_LOADING,
            soundAction = Event(PlaybackAction.PREPARE)
        )

        val newState = sut.map(state, action)

        assertEquals(PlaybackAction.PREPARE, newState.soundAction.peekContent())
    }

    @Test
    fun `GIVEN action is SoundStateChanged_SOUND_INVALID WHEN map THEN update state`() {
        val action = Action.SoundStateChanged(SoundState.SOUND_INVALID)
        val state = State.default()

        val newState = sut.map(state, action)

        assertEquals(SoundState.SOUND_INVALID, newState.soundState)
    }

    @Test
    fun `GIVEN action is SoundStateChanged_SOUND_ERROR WHEN map THEN update state`() {
        val action = Action.SoundStateChanged(SoundState.SOUND_ERROR)
        val state = State.default()

        val newState = sut.map(state, action)

        assertEquals(SoundState.SOUND_ERROR, newState.soundState)
    }

    @Test
    fun `GIVEN action is SoundStateChanged_SOUND_LOADING WHEN map THEN update state`() {
        val action = Action.SoundStateChanged(SoundState.SOUND_LOADING)
        val state = State.default()

        val newState = sut.map(state, action)

        assertEquals(SoundState.SOUND_LOADING, newState.soundState)
    }

    @Test
    fun `GIVEN action is SoundStateChanged_SOUND_OK AND gifState was OK AND soundState was not OK WHEN map THEN start sound`() {
        val action = Action.SoundStateChanged(SoundState.SOUND_OK)
        val state = State.default(gifState = GifState.GIF_OK, soundState = SoundState.SOUND_LOADING)

        val newState = sut.map(state, action)

        assertEquals(Event(PlaybackAction.PLAY), newState.soundAction)
    }

    @Test
    fun `GIVEN action is SoundStateChanged_SOUND_OK AND gifState was not OK WHEN map THEN keep previous soundAction`() {
        val action = Action.SoundStateChanged(SoundState.SOUND_OK)
        val state = State.default(
            gifState = GifState.GIF_LOADING,
            soundState = SoundState.SOUND_LOADING,
            soundAction = Event(PlaybackAction.PREPARE)
        )

        val newState = sut.map(state, action)

        assertEquals(PlaybackAction.PREPARE, newState.soundAction.peekContent())
    }

    @Test
    fun `GIVEN action is SoundStateChanged_SOUND_STARTED WHEN map THEN start Gif`() {
        val action = Action.SoundStateChanged(SoundState.SOUND_STARTED)
        val state = State.default()

        val newState = sut.map(state, action)

        assertEquals(Event(PlaybackAction.PLAY), newState.gifAction)
    }

    @Test
    fun `GIVEN action is OnUserAction_ON_PLAYGIFLABEL WHEN map THEN start Gif`() {
        val action = Action.OnUserAction(UserAction.ON_PLAYGIFLABEL)
        val state = State.default()

        val newState = sut.map(state, action)

        assertEquals(Event(PlaybackAction.PLAY), newState.gifAction)
    }

    @Test
    fun `GIVEN action is OnUserAction_ON_OFFSET_INCREASE WHEN map THEN increase offset and restart Gif`() {
        val action = Action.OnUserAction(UserAction.ON_OFFSET_INCREASE)
        val state = State.default(currentSecondsOffset = 1)

        val newState = sut.map(state, action)

        assertEquals(
            state.copy(currentSecondsOffset = 2, soundAction = Event(PlaybackAction.RESTART)),
            newState
        )
    }

    @Test
    fun `GIVEN action is OnUserAction_ON_OFFSET_DECREASE WHEN map THEN decrease offset and restart Gif`() {
        val action = Action.OnUserAction(UserAction.ON_OFFSET_DECREASE)
        val state = State.default(currentSecondsOffset = 1)

        val newState = sut.map(state, action)

        assertEquals(
            state.copy(currentSecondsOffset = 0, soundAction = Event(PlaybackAction.RESTART)),
            newState
        )
    }

    @Test
    fun `GIVEN action is OnUserAction_ON_OFFSET_RESET WHEN map THEN reset offset to original and restart Gif`() {
        val action = Action.OnUserAction(UserAction.ON_OFFSET_RESET)
        val state = State.default(currentSecondsOffset = 5, soundSource = SoundSource("", 4))

        val newState = sut.map(state, action)

        assertEquals(
            state.copy(currentSecondsOffset = 4, soundAction = Event(PlaybackAction.RESTART)),
            newState
        )
    }

    @Test
    fun `GIVEN action is OnUserAction_ON_REFRESH WHEN map THEN restart Gif`() {
        val action = Action.OnUserAction(UserAction.ON_REFRESH)
        val state = State.default(currentSecondsOffset = 5, soundSource = SoundSource("", 4))

        val newState = sut.map(state, action)

        assertEquals(
            state.copy(currentSecondsOffset = 5, soundAction = Event(PlaybackAction.RESTART)),
            newState
        )
    }

    @Test
    fun `GIVEN action is FragmentCreated WHEN map THEN prepare gif and sound again`() {
        val action = Action.FragmentCreated
        val state = State.default(soundAction = Event(PlaybackAction.PLAY), gifAction = Event(PlaybackAction.PLAY))

        val newState = sut.map(state, action)

        assertEquals(
            state.copy(gifAction = Event(PlaybackAction.PREPARE), soundAction = Event(PlaybackAction.PREPARE)),
            newState
        )
    }
}
