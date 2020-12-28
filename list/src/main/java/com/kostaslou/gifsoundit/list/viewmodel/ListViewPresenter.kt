package com.kostaslou.gifsoundit.list.viewmodel

import com.kostaslou.gifsoundit.list.ListContract
import com.loukwn.postdata.FilterType

object ListViewPresenter {
    fun updateView(
        view: ListContract.View?,
        oldState: ListViewModel.State,
        newState: ListViewModel.State
    ) {
        if (view != null) {
            if (newState.filterMenuIsVisible != oldState.filterMenuIsVisible) {
                if (newState.filterMenuIsVisible) view.showFilterMenu() else view.hideFilterMenu()
            }

            if (newState.filterType != oldState.filterType) {
                when (newState.filterType) {
                    FilterType.Hot -> view.setFilterMenuToHot()
                    FilterType.New -> view.setFilterMenuToNew()
                    is FilterType.Top -> view.setFilterMenuToTop()
                }
                view.hideFilterMenu()
            }

            if (newState.isErrored != oldState.isErrored) {
                view.showErrorToast()
            }

            if (newState.isLoading) {
                view.allowOrNotSwipeToRefresh(allow = false)
                view.allowOrNotScrollToBottomLoading(allow = false)
            } else {
                view.allowOrNotSwipeToRefresh(allow = true)
                view.allowOrNotScrollToBottomLoading(allow = true)
            }


            view.showList(newState.adapterData)
        }
    }
}
