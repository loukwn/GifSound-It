package com.loukwn.gifsoundit.settings.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import com.loukwn.gifsoundit.settings.R
import com.loukwn.gifsoundit.settings.SettingsContract
import com.loukwn.gifsoundit.settings.State
import javax.inject.Inject

internal class SettingsViewPresenter @Inject constructor() {
    fun updateView(view: SettingsContract.View, state: State) {
        if (state.modeSelectorCollapsed) view.collapseModeSelector() else view.expandModeSelector()
        if (state.aboutCollapsed) view.collapseAbout() else view.expandAbout()

        when (state.currentMode) {
            AppCompatDelegate.MODE_NIGHT_NO -> {
                view.selectLightMode()
                view.setModeSelectedStringRes(textRes = R.string.settings_theme_mode_light)
            }
            AppCompatDelegate.MODE_NIGHT_YES -> {
                view.selectDarkMode()
                view.setModeSelectedStringRes(textRes = R.string.settings_theme_mode_dark)
            }
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                view.selectSystemDefaultMode()
                view.setModeSelectedStringRes(textRes = R.string.settings_theme_mode_system)
            }
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY -> {
                view.selectBatterySaverMode()
                view.setModeSelectedStringRes(textRes = R.string.settings_theme_mode_battery)
            }
        }
    }
}
