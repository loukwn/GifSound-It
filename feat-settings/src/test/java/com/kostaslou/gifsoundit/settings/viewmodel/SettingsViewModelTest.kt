package com.kostaslou.gifsoundit.settings.viewmodel

import com.kostaslou.gifsoundit.common.disk.SharedPrefsHelper
import com.kostaslou.gifsoundit.common.disk.getDayNightMode
import com.kostaslou.gifsoundit.settings.Action
import com.kostaslou.gifsoundit.settings.SettingsContract
import com.loukwn.navigation.Navigator
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

internal class SettingsViewModelTest {

    private lateinit var sut: SettingsViewModel

    private lateinit var sharedPrefsHelper: SharedPrefsHelper

    @MockK
    lateinit var navigator: Navigator

    @MockK
    lateinit var settingsStateReducer: SettingsStateReducer

    @MockK
    lateinit var settingsViewPresenter: SettingsViewPresenter

    private val trampolineScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        sharedPrefsHelper = mockk(relaxUnitFun = true) { every { getDayNightMode() } returns 1 }

        sut = SettingsViewModel(
            sharedPrefsHelper,
            navigator,
            settingsStateReducer,
            settingsViewPresenter,
            trampolineScheduler,
            trampolineScheduler
        )
    }

    @Test
    fun `GIVEN view is set WHEN onStart THEN set view listener to this`() {
        val view = mockk<SettingsContract.View>(relaxed = true)
        sut.setView(view)

        sut.doOnStart()

        verify(exactly = 1) { view.setListener(sut) }
    }

    @Test
    fun `GIVEN view is set WHEN onStop THEN remove view listener`() {
        val view = mockk<SettingsContract.View>(relaxed = true)
        sut.setView(view)

        sut.doOnStop()

        verify(exactly = 1) { view.removeListener(sut) }
    }

    @Test
    fun `WHEN fragment is recreated THEN send an event to set the state again`() {
        sut.doOnCreate()
        sut.doOnCreate() // recreation

        verify(exactly = 1) { settingsStateReducer.map(any(), Action.Created) }
    }

    @Test
    fun `GIVEN view is set WHEN onModeSelected THEN make sure reducer gets the action`() {
        val view = mockk<SettingsContract.View>(relaxed = true)
        sut.setView(view)

        sut.onModeSelected(1)

        verify(exactly = 1) { settingsStateReducer.map(any(), Action.ModeSelected(1)) }
    }

    @Test
    fun `GIVEN view is set WHEN onModeSelectorBgClicked THEN make sure reducer gets the action`() {
        val view = mockk<SettingsContract.View>(relaxed = true)
        sut.setView(view)

        sut.onModeSelectorBgClicked()

        verify(exactly = 1) { settingsStateReducer.map(any(), Action.ModeBgClicked) }
    }

    @Test
    fun `GIVEN view is set WHEN aboutBgClicked THEN make sure reducer gets the action`() {
        val view = mockk<SettingsContract.View>(relaxed = true)
        sut.setView(view)

        sut.onAboutBgClicked()

        verify(exactly = 1) { settingsStateReducer.map(any(), Action.AboutBgClicked) }
    }

    @Test
    fun `GIVEN view is set WHEN backPressed THEN navigate back`() {
        val view = mockk<SettingsContract.View>(relaxed = true)
        sut.setView(view)

        sut.onBackButtonPressed()

        verify(exactly = 1) { navigator.goBack() }
    }

    @Test
    fun `GIVEN view is set WHEN ossContainerClicked THEN navigate back`() {
        val view = mockk<SettingsContract.View>(relaxed = true)
        sut.setView(view)

        sut.onOssContainerClicked()

        verify(exactly = 1) { navigator.navigateToOssLicenses() }
    }

    @Test
    fun `WHEN viewmodel is cleared THEN nothing weird happens`() {
        sut.onCleared()
    }
}
