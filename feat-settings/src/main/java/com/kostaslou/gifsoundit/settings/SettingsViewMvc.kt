package com.kostaslou.gifsoundit.settings

import com.kostaslou.gifsoundit.settings.controller.SettingsUiModel
import com.kostaslou.gifsoundit.common.contract.ActionableViewContract

interface SettingsContract {

    interface View : ActionableViewContract<Listener> {
        fun setDataToViews(uiModel: SettingsUiModel)
    }

    interface Listener {
        fun onBackButtonPressed()
        fun onThemeSelected(mode: Int)
    }
}
