package com.kostaslou.gifsoundit.screens.settings.view

import com.kostaslou.gifsoundit.common.ActionableViewMvc
import com.kostaslou.gifsoundit.screens.settings.controller.SettingsUiModel

interface SettingsViewMvc: ActionableViewMvc<SettingsViewMvc.Listener> {

    interface Listener {
        fun onBackButtonPressed()
        fun onThemeSelected(mode: Int)
    }

    fun setDataToViews(uiModel: SettingsUiModel)
}
