package com.kostaslou.gifsoundit.opengs.viewmodel

import android.content.res.Resources
import com.gifsoundit.opengs.R
import com.kostaslou.gifsoundit.common.util.Event
import com.kostaslou.gifsoundit.opengs.GifSource
import com.kostaslou.gifsoundit.opengs.GifState
import com.kostaslou.gifsoundit.opengs.GifType
import com.kostaslou.gifsoundit.opengs.OpenGSContract
import com.kostaslou.gifsoundit.opengs.PlaybackAction
import com.kostaslou.gifsoundit.opengs.SoundSource
import com.kostaslou.gifsoundit.opengs.SoundState
import com.kostaslou.gifsoundit.opengs.State
import com.kostaslou.gifsoundit.opengs.default
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import java.lang.IllegalStateException

internal class OpenGSViewPresenterTest {
    private val resources = mockk<Resources>(relaxed = true)
    private val sut = OpenGSViewPresenter(resources)
    private val view = mockk<OpenGSContract.View>(relaxUnitFun = true)

    @Test
    fun `WHEN gifState is GIF_OK AND soundState is SOUND_STARTED then show playback ui`() {
        sut.updateView(
            view,
            state = State.default(gifState = GifState.GIF_OK, soundState = SoundState.SOUND_STARTED)
        )

        verify(exactly = 1) { view.setVideoOffsetControlsEnabled(true) }
        verify(exactly = 1) { view.showRefreshButton() }
        verify(exactly = 1) { view.showOffsetSeconds(any()) }
    }

    @Test
    fun `WHEN gifState is not GIF_OK then hide playback ui`() {
        sut.updateView(view, state = State.default(gifState = GifState.GIF_ERROR))

        verify(exactly = 1) { view.setVideoOffsetControlsEnabled(false) }
        verify(exactly = 0) { view.showRefreshButton() }
        verify(exactly = 0) { view.showOffsetSeconds(any()) }
    }

    @Test
    fun `WHEN soundState is not SOUND_STARTED then hide playback ui`() {
        sut.updateView(view, state = State.default(soundState = SoundState.SOUND_ERROR))

        verify(exactly = 1) { view.setVideoOffsetControlsEnabled(false) }
        verify(exactly = 0) { view.showRefreshButton() }
        verify(exactly = 0) { view.showOffsetSeconds(any()) }
    }

    @Test
    fun `WHEN gifState is GIF_OK and soundState is SOUND_ERROR then show showGIFLayout`() {
        sut.updateView(
            view,
            state = State.default(gifState = GifState.GIF_OK, soundState = SoundState.SOUND_ERROR)
        )

        verify(exactly = 1) { view.setShowGIFLayoutVisibitity(true) }
    }

    @Test
    fun `WHEN gifState is GIF_OK and soundState is SOUND_INVALID then show showGIFLayout`() {
        sut.updateView(
            view,
            state = State.default(gifState = GifState.GIF_OK, soundState = SoundState.SOUND_INVALID)
        )

        verify(exactly = 1) { view.setShowGIFLayoutVisibitity(true) }
    }

    @Test
    fun `WHEN gifState is not GIF_OK THEN hide showGIFLayout`() {
        sut.updateView(view, state = State.default(gifState = GifState.GIF_ERROR))

        verify(exactly = 1) { view.setShowGIFLayoutVisibitity(false) }
    }

    @Test
    fun `WHEN soundState is SOUND_OK THEN hide showGIFLayout`() {
        sut.updateView(view, state = State.default(soundState = SoundState.SOUND_OK))

        verify(exactly = 1) { view.setShowGIFLayoutVisibitity(false) }
    }

    @Test
    fun `WHEN SoundAction PREPARE AND soundUrl is not null AND Gif is not from YouTube THEN prepareSoundYoutubeView`() {
        val state = State.default(
            soundAction = Event(PlaybackAction.PREPARE),
            soundSource = SoundSource(soundUrl = "url", defaultSecondsOffset = 1),
            gifSource = GifSource(gifType = GifType.GIF, gifUrl = "url")
        )

        sut.updateView(view, state)

        verify(exactly = 1) { view.prepareSoundYoutubeView(any(), any()) }
    }

    @Test
    fun `WHEN SoundAction PREPARE AND soundUrl is null AND Gif is not from YouTube THEN do not prepareSoundYoutubeView`() {
        val state = State.default(
            soundAction = Event(PlaybackAction.PREPARE),
            soundSource = SoundSource(soundUrl = null, defaultSecondsOffset = 1),
            gifSource = GifSource(gifType = GifType.GIF, gifUrl = "url")
        )

        sut.updateView(view, state)

        verify(exactly = 0) { view.prepareSoundYoutubeView(any(), any()) }
    }

    @Test
    fun `WHEN SoundAction PREPARE AND soundUrl is not null AND Gif is from YouTube THEN do not prepareSoundYoutubeView`() {
        val state = State.default(
            soundAction = Event(PlaybackAction.PREPARE),
            soundSource = SoundSource(soundUrl = "url", defaultSecondsOffset = 1),
            gifSource = GifSource(gifType = GifType.YOUTUBE, gifUrl = "url")
        )

        sut.updateView(view, state)

        verify(exactly = 0) { view.prepareSoundYoutubeView(any(), any()) }
    }

    @Test
    fun `WHEN SoundAction PLAY THEN start sound`() {
        val state = State.default(soundAction = Event(PlaybackAction.PLAY))

        sut.updateView(view, state)

        verify(exactly = 1) { view.startSound() }
    }

    @Test
    fun `WHEN SoundAction RESTART THEN restart sound`() {
        val state = State.default(soundAction = Event(PlaybackAction.RESTART))

        sut.updateView(view, state)

        verify(exactly = 1) { view.seekAndRestartSound(any()) }
    }

    @Test
    fun `WHEN SoundAction already handled THEN do nothing to the sound`() {
        val state =
            State.default(soundAction = Event(PlaybackAction.PLAY).apply { getContentIfNotHandled() })

        sut.updateView(view, state)

        verify(exactly = 0) { view.prepareSoundYoutubeView(any(), any()) }
        verify(exactly = 0) { view.startSound() }
        verify(exactly = 0) { view.seekAndRestartSound(any()) }
    }

    @Test
    fun `WHEN GifAction PREPARE AND gifType is GIF THEN prepareGifImage`() {
        val state = State.default(
            gifAction = Event(PlaybackAction.PREPARE),
            gifSource = GifSource(gifType = GifType.GIF, gifUrl = "url")
        )

        sut.updateView(view, state)

        verify(exactly = 1) { view.prepareGifImage(any()) }
    }

    @Test
    fun `WHEN GifAction PREPARE AND gifType is MP4 THEN prepareGifVideo`() {
        val state = State.default(
            gifAction = Event(PlaybackAction.PREPARE),
            gifSource = GifSource(gifType = GifType.MP4, gifUrl = "url")
        )

        sut.updateView(view, state)

        verify(exactly = 1) { view.prepareGifVideo(any()) }
    }

    @Test
    fun `WHEN GifAction PREPARE AND gifType is YOUTUBE THEN showYoutubeErrorScreen`() {
        val state = State.default(
            gifAction = Event(PlaybackAction.PREPARE),
            gifSource = GifSource(gifType = GifType.YOUTUBE, gifUrl = "url")
        )

        sut.updateView(view, state)

        verify(exactly = 1) { view.showYoutubeErrorScreen() }
    }

    @Test
    fun `WHEN GifAction PLAY AND gifType is GIF THEN startGifImageFromTheStart`() {
        val state = State.default(
            gifAction = Event(PlaybackAction.PLAY),
            gifSource = GifSource(gifType = GifType.GIF, gifUrl = "url")
        )

        sut.updateView(view, state)

        verify(exactly = 1) { view.startGifImageFromTheStart() }
    }

    @Test
    fun `WHEN GifAction PLAY AND gifType is MP4 THEN startGifVideoFromTheStart`() {
        val state = State.default(
            gifAction = Event(PlaybackAction.PLAY),
            gifSource = GifSource(gifType = GifType.MP4, gifUrl = "url")
        )

        sut.updateView(view, state)

        verify(exactly = 1) { view.startGifVideoFromTheStart() }
    }

    @Test
    fun `WHEN GifAction PLAY AND gifType is YOUTUBE THEN do not start anything`() {
        val state = State.default(
            gifAction = Event(PlaybackAction.PLAY),
            gifSource = GifSource(gifType = GifType.YOUTUBE, gifUrl = "url")
        )

        sut.updateView(view, state)

        verify(exactly = 0) { view.startGifImageFromTheStart() }
        verify(exactly = 0) { view.startGifVideoFromTheStart() }
    }

    @Test
    fun `WHEN GifAction RESTART AND gifType is GIF THEN startGifImageFromTheStart`() {
        val state = State.default(
            gifAction = Event(PlaybackAction.RESTART),
            gifSource = GifSource(gifType = GifType.GIF, gifUrl = "url")
        )

        sut.updateView(view, state)

        verify(exactly = 1) { view.startGifImageFromTheStart() }
    }

    @Test
    fun `WHEN GifAction RESTART AND gifType is MP4 THEN startGifVideoFromTheStart`() {
        val state = State.default(
            gifAction = Event(PlaybackAction.RESTART),
            gifSource = GifSource(gifType = GifType.MP4, gifUrl = "url")
        )

        sut.updateView(view, state)

        verify(exactly = 1) { view.startGifVideoFromTheStart() }
    }

    @Test
    fun `WHEN GifAction RESTART AND gifType is YOUTUBE THEN do not start anything`() {
        val state = State.default(
            gifAction = Event(PlaybackAction.RESTART),
            gifSource = GifSource(gifType = GifType.YOUTUBE, gifUrl = "url")
        )

        sut.updateView(view, state)

        verify(exactly = 0) { view.startGifImageFromTheStart() }
        verify(exactly = 0) { view.startGifVideoFromTheStart() }
    }

    @Test(expected = IllegalStateException::class)
    fun `WHEN gifUrl is null THEN throw IllegalStateException`() {
        val state = State.default(
            gifAction = Event(PlaybackAction.PREPARE),
            gifSource = GifSource(gifType = GifType.MP4, gifUrl = null)
        )

        sut.updateView(view, state)
    }

    @Test
    fun `WHEN GifAction already handled THEN do nothing to the sound`() {
        val state =
            State.default(gifAction = Event(PlaybackAction.PLAY).apply { getContentIfNotHandled() })

        sut.updateView(view, state)

        verify(exactly = 0) { view.prepareGifImage(any()) }
        verify(exactly = 0) { view.prepareGifVideo(any()) }
        verify(exactly = 0) { view.showYoutubeErrorScreen() }
        verify(exactly = 0) { view.startGifImageFromTheStart() }
        verify(exactly = 0) { view.startGifVideoFromTheStart() }
    }

    @Test
    fun `WHEN state is updated THEN update the status message`() {
        val state = State.default(gifState = GifState.GIF_ERROR, soundState = SoundState.SOUND_OK)

        sut.updateView(view, state)

        verify(exactly = 1) { resources.getString(state.gifState.errorTextRes) }
        verify(exactly = 1) { resources.getString(state.soundState.errorTextRes) }
        verify(exactly = 1) { resources.getString(R.string.opengs_status_message, any(), any()) }
    }
}
