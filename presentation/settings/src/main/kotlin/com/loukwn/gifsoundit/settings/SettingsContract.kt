package com.loukwn.gifsoundit.settings

import androidx.annotation.StringRes
import com.loukwn.gifsoundit.presentation.common.contract.ActionableViewContract

internal interface SettingsContract {

    interface View : ActionableViewContract<Listener> {
        fun collapseModeSelector()
        fun expandModeSelector()
        fun collapseAbout()
        fun expandAbout()
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
        fun onAboutBgClicked()
        fun onOssContainerClicked()
    }

    interface ViewModel {
        fun setView(view: View)
    }
}

internal data class State(
    val modeSelectorCollapsed: Boolean,
    val aboutCollapsed: Boolean,
    val currentMode: Int
)

internal sealed class Action {
    data class ModeSelected(val mode: Int) : Action()
    object AboutBgClicked : Action()
    object ModeBgClicked : Action()
    object Created : Action()
}
