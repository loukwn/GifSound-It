package com.kostaslou.gifsoundit.settings

import androidx.annotation.StringRes
import com.kostaslou.gifsoundit.common.contract.ActionableViewContract

internal interface SettingsContract {

    interface View : ActionableViewContract<Listener> {
        fun collapseModeSelector()
        fun expandModeSelector()
        fun selectLightMode()
        fun selectDarkMode()
        fun selectSystemDefaultMode()
        fun selectBatterySaverMode()
        fun setModeSelectedStringRes(@StringRes textRes: Int)
    }

    interface Listener {
        fun onBackButtonPressed()
        fun onModeSelected(mode: Int)
        fun onModeSelectorBgClicked()
    }

    interface ViewModel {
        fun setView(view: View)
    }
}

internal data class State(
    val modeSelectorCollapsed: Boolean,
    val currentMode: Int
)

internal sealed class Action {
    data class ModeSelected(val mode: Int) : Action()
    object ModeBgClicked : Action()
    object Created : Action()
}
