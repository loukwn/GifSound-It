package com.kostaslou.gifsoundit.list.viewmodel

import com.kostaslou.gifsoundit.list.ListContract
import com.kostaslou.gifsoundit.list.State
import com.loukwn.postdata.FilterType

object ListViewPresenter {
    fun updateView(
        view: ListContract.View,
        state: State
    ) {
        state.filterMenuIsVisible.getContentIfNotHandled()?.let { menuIsVisible ->
            if (menuIsVisible) view.showFilterMenu() else view.hideFilterMenu()
        }

        state.filterType.getContentIfNotHandled()?.let { filterType ->
            when (filterType) {
                FilterType.Hot -> view.setFilterMenuToHot()
                FilterType.New -> view.setFilterMenuToNew()
                is FilterType.Top -> view.setFilterMenuToTop()
            }
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

        view.showList(state.adapterData)
    }
}
