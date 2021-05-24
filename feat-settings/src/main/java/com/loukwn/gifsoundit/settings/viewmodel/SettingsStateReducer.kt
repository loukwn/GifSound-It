package com.loukwn.gifsoundit.settings.viewmodel

import com.loukwn.gifsoundit.settings.Action
import com.loukwn.gifsoundit.settings.State
import javax.inject.Inject

internal class SettingsStateReducer @Inject constructor() {
    fun reduce(state: State, action: Action): State {
        return when (action) {
            is Action.ModeSelected -> state.copy(currentMode = action.mode)
            Action.ModeBgClicked -> state.copy(modeSelectorCollapsed = !state.modeSelectorCollapsed)
            Action.AboutBgClicked -> state.copy(aboutCollapsed = !state.aboutCollapsed)
            Action.Created -> state
        }
    }
}
