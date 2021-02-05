package com.kostaslou.gifsoundit.settings.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import com.kostaslou.gifsoundit.settings.SettingsContract
import com.kostaslou.gifsoundit.settings.State
import com.loukwn.feat_settings.R
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

internal class SettingsViewPresenterTest {
    private val sut = SettingsViewPresenter()

    @MockK
    lateinit var view : SettingsContract.View

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun `GIVEN state has modeSelectorCollapsed true WHEN map THEN collapse selector in view`() {
        val state = State(modeSelectorCollapsed = true, currentMode = 1)

        sut.updateView(view, state)

        verify(exactly = 1) { view.collapseModeSelector() }
    }

    @Test
    fun `GIVEN state has modeSelectorCollapsed false WHEN map THEN expand selector in view`() {
        val state = State(modeSelectorCollapsed = false, currentMode = 1)

        sut.updateView(view, state)

        verify(exactly = 1) { view.expandModeSelector() }
    }

    @Test
    fun `GIVEN state has MODE_NIGHT_NO as mode WHEN map THEN go with light mode`() {
        val state = State(modeSelectorCollapsed = false, currentMode = AppCompatDelegate.MODE_NIGHT_NO)

        sut.updateView(view, state)

        verify(exactly = 1) { view.selectLightMode() }
        verify(exactly = 1) { view.setModeSelectedStringRes(R.string.settings_theme_mode_light) }
    }

    @Test
    fun `GIVEN state has MODE_NIGHT_YES as mode WHEN map THEN go with dark mode`() {
        val state = State(modeSelectorCollapsed = false, currentMode = AppCompatDelegate.MODE_NIGHT_YES)

        sut.updateView(view, state)

        verify(exactly = 1) { view.selectDarkMode() }
        verify(exactly = 1) { view.setModeSelectedStringRes(R.string.settings_theme_mode_dark) }
    }

    @Test
    fun `GIVEN state has MODE_NIGHT_FOLLOW_SYSTEM as mode WHEN map THEN go with system default mode`() {
        val state = State(modeSelectorCollapsed = false, currentMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        sut.updateView(view, state)

        verify(exactly = 1) { view.selectSystemDefaultMode() }
        verify(exactly = 1) { view.setModeSelectedStringRes(R.string.settings_theme_mode_system) }
    }

    @Test
    fun `GIVEN state has MODE_NIGHT_AUTO_BATTERY as mode WHEN map THEN go with battery mode`() {
        val state = State(modeSelectorCollapsed = false, currentMode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)

        sut.updateView(view, state)

        verify(exactly = 1) { view.selectBatterySaverMode() }
        verify(exactly = 1) { view.setModeSelectedStringRes(R.string.settings_theme_mode_battery) }
    }
}
