package com.loukwn.gifsoundit.create

import com.loukwn.gifsoundit.common.contract.ActionableViewContract

internal interface CreateContract {
    interface View: ActionableViewContract<Listener> {
        fun setUiModel(uiModel: UiModel)
    }

    interface Listener {
        fun onGoPressed() {}
    }

    interface ViewModel {

    }

//    data class UiModel(
//
//    )
}