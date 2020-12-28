package com.kostaslou.gifsoundit.list

import com.kostaslou.gifsoundit.common.contract.ActionableViewContract
import com.kostaslou.gifsoundit.list.view.adapter.ListAdapterModel
import com.loukwn.postdata.TopFilterType

interface ListContract {

    interface View : ActionableViewContract<Listener> {
        fun allowOrNotSwipeToRefresh(allow: Boolean)
        fun allowOrNotScrollToBottomLoading(allow: Boolean)
        fun showList(data: List<ListAdapterModel>)
        fun showEmptyScreen()
        fun showFilterMenu()
        fun hideFilterMenu()
        fun setFilterMenuToHot()
        fun setFilterMenuToNew()
        fun setFilterMenuToTop()
        fun showErrorToast()
    }

    interface Listener {
        fun onSwipeToRefresh()
        fun onScrolledToBottom()
        fun onListItemClicked(post: ListAdapterModel.Post)
        fun onHotFilterSelected()
        fun onNewFilterSelected()
        fun onTopFilterSelected(type: TopFilterType)
        fun onMoreMenuButtonClicked()
    }

    interface ViewModel {
        fun setView(view: View)
    }
}
