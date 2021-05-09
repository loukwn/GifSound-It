package com.kostaslou.gifsoundit.list.viewmodel

import com.kostaslou.gifsoundit.list.ListContract
import com.kostaslou.gifsoundit.list.State
import javax.inject.Inject

internal class ListViewPresenter @Inject constructor() {
    fun updateView(
        view: ListContract.View,
        state: State
    ) {
        if (state.optionsLayoutIsOpen) {
            view.showOptionsLayout(sourceType = state.sourceType, filterType = state.filterType)
            view.showOverlay()
        } else {
            view.hideOptionsLayout()
            view.hideOverlay()
        }

        state.errorMessage?.getContentIfNotHandled()?.let { error ->
            view.showErrorToast(error)
        }

        if (state.isLoading) {
            view.allowOrNotSwipeToRefresh(allow = false)
            view.allowOrNotScrollToBottomLoading(allow = false)
        } else {
            view.allowOrNotSwipeToRefresh(allow = true)
            view.allowOrNotScrollToBottomLoading(allow = true)
        }

        view.setLoadingScreenVisibility(isVisible = state.isLoading && state.adapterData.isEmpty())
        view.showList(state.adapterData)
    }
}
