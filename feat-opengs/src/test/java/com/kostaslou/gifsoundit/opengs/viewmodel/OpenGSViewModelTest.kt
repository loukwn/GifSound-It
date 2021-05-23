package com.kostaslou.gifsoundit.opengs.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.kostaslou.gifsoundit.opengs.Action
import com.kostaslou.gifsoundit.opengs.GifState
import com.kostaslou.gifsoundit.opengs.OpenGSContract
import com.kostaslou.gifsoundit.opengs.SoundState
import com.kostaslou.gifsoundit.opengs.State
import com.kostaslou.gifsoundit.opengs.UserAction
import com.kostaslou.gifsoundit.opengs.default
import com.kostaslou.gifsoundit.opengs.mappers.QueryToStateMapper
import com.loukwn.navigation.Navigator
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

internal class OpenGSViewModelTest {

    private val navigator = mockk<Navigator>(relaxUnitFun = true)
    private val stateMapper = mockk<QueryToStateMapper>(relaxed = true)
    private val reducer = mockk<OpenGSStateReducer>(relaxed = true)
    private val viewPresenter = mockk<OpenGSViewPresenter>(relaxed = true)
    private val handle = SavedStateHandle(
        mapOf(
            Navigator.PARAM_OPENGS_QUERY to "",
            Navigator.PARAM_OPENGS_FROM_DEEP_LINK to false,
        )
    )

    private val trampolineScheduler = Schedulers.trampoline()

    private val sut by lazy {
        OpenGSViewModel(
            navigator = navigator,
            queryToStateMapper = stateMapper,
            ioScheduler = trampolineScheduler,
            uiScheduler = trampolineScheduler,
            openGSStateReducer = reducer,
            openGSViewPresenter = viewPresenter,
            handle = handle
        )
    }

    @Before
    fun setup() {
        every { stateMapper.getState(any(), any()) } returns State.default()
    }

    @Test
    fun `WHEN onBackButtonPressed THEN go to previous screen via navigator`() {
        sut.onBackButtonPressed()
        verify(exactly = 1) { navigator.goBack() }
    }

    @Test
    fun `WHEN onRefreshButtonPressed THEN send action to reducer`() {
        sut.onRefreshButtonPressed()
        verify(exactly = 1) { reducer.reduce(any(), Action.OnUserAction(UserAction.ON_REFRESH)) }
    }

    @Test
    fun `WHEN onShareButtonPressed THEN navigate to share sheet with correct query`() {
        handle.set<String>(Navigator.PARAM_OPENGS_QUERY, "query")

        sut.onShareButtonPressed()

        verify(exactly = 1) { navigator.openShareScreen("query") }
    }

    @Test
    fun `WHEN onPlayGifLabelPressed THEN send action to reducer`() {
        sut.onPlayGifLabelPressed()
        verify(exactly = 1) { reducer.reduce(any(), Action.OnUserAction(UserAction.ON_PLAYGIFLABEL)) }
    }

    @Test
    fun `WHEN onOffsetIncreaseButtonPressed THEN send action to reducer`() {
        sut.onOffsetIncreaseButtonPressed()
        verify(exactly = 1) {
            reducer.reduce(
                any(),
                Action.OnUserAction(UserAction.ON_OFFSET_INCREASE)
            )
        }
    }

    @Test
    fun `WHEN onOffsetDecreaseButtonPressed THEN send action to reducer`() {
        sut.onOffsetDecreaseButtonPressed()
        verify(exactly = 1) {
            reducer.reduce(
                any(),
                Action.OnUserAction(UserAction.ON_OFFSET_DECREASE)
            )
        }
    }

    @Test
    fun `WHEN onOffsetResetButtonPressed THEN send action to reducer`() {
        sut.onOffsetResetButtonPressed()
        verify(exactly = 1) { reducer.reduce(any(), Action.OnUserAction(UserAction.ON_OFFSET_RESET)) }
    }

    @Test
    fun `WHEN onGifStateChanged THEN send action to reducer`() {
        val gifState = mockk<GifState>()
        sut.onGifStateChanged(gifState)
        verify(exactly = 1) { reducer.reduce(any(), Action.GifStateChanged(gifState)) }
    }

    @Test
    fun `WHEN onSoundStateChanged THEN send action to reducer`() {
        val soundState = mockk<SoundState>()
        sut.onSoundStateChanged(soundState)
        verify(exactly = 1) { reducer.reduce(any(), Action.SoundStateChanged(soundState)) }
    }

    @Test
    fun `GIVEN view is set WHEN onStart THEN set view listener to this`() {
        val view = mockk<OpenGSContract.View>(relaxed = true)
        sut.setView(view)

        sut.doOnStart()

        verify(exactly = 1) { view.setListener(sut) }
    }

    @Test
    fun `GIVEN view is set WHEN onStop THEN remove view listener`() {
        val view = mockk<OpenGSContract.View>(relaxed = true)
        sut.setView(view)

        sut.doOnStop()

        verify(exactly = 1) { view.removeListener(sut) }
    }

    @Test
    fun `GIVEN view is set WHEN onDestroy THEN release`() {
        val view = mockk<OpenGSContract.View>(relaxed = true)
        sut.setView(view)

        sut.doOnDestroy()

        verify(exactly = 1) { view.release() }
    }

    @Test
    fun `WHEN fragment is recreated THEN send an event to set the state again`() {
        sut.doOnCreate()
        sut.doOnCreate() // recreation

        verify(exactly = 1) { reducer.reduce(any(), Action.FragmentCreated) }
    }
}
