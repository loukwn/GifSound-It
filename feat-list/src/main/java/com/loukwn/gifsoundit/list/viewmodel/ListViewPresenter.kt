package com.loukwn.gifsoundit.list.viewmodel

import android.content.res.Resources
import com.loukwn.gifsoundit.list.ListContract
import com.loukwn.gifsoundit.list.R
import com.loukwn.gifsoundit.list.State
import javax.inject.Inject

internal class ListViewPresenter @Inject constructor(
    private val resources: Resources,
) {
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

        state.errorMessage?.getContentIfNotHandled()?.let {
            val postErrorMessage = resources.getString(R.string.list_error_posts)
            view.showErrorToast(postErrorMessage)
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
