package com.loukwn.feat_settings

import com.kostaslou.gifsoundit.common.contract.ActionableViewContract
import com.loukwn.feat_settings.controller.SettingsUiModel

interface SettingsContract {

    interface View : ActionableViewContract<Listener> {
        fun setDataToViews(uiModel: SettingsUiModel)
    }

    interface Listener {
        fun onBackButtonPressed()
        fun onThemeSelected(mode: Int)
    }
}
