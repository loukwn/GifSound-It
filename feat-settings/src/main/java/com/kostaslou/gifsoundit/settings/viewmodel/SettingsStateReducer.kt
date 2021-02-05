package com.kostaslou.gifsoundit.settings.viewmodel

import com.kostaslou.gifsoundit.settings.Action
import com.kostaslou.gifsoundit.settings.State
import javax.inject.Inject

internal class SettingsStateReducer @Inject constructor(){
    fun map(state: State, action: Action): State {
        return when (action) {
            is Action.ModeSelected -> state.copy(currentMode = action.mode)
            Action.ModeBgClicked -> state.copy(modeSelectorCollapsed = !state.modeSelectorCollapsed)
            Action.Created -> state
        }
    }
}
