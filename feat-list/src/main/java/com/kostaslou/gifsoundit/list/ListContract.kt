package com.kostaslou.gifsoundit.list

import com.kostaslou.gifsoundit.common.contract.ActionableViewContract
import com.kostaslou.gifsoundit.common.util.DataState
import com.kostaslou.gifsoundit.common.util.Event
import com.kostaslou.gifsoundit.list.view.adapter.ListAdapterModel
import com.loukwn.postdata.FilterType
import com.loukwn.postdata.TopFilterType
import com.loukwn.postdata.model.domain.PostResponse

internal interface ListContract {

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
        fun showErrorToast(errorMessage: String)
    }

    interface Listener {
        fun onSwipeToRefresh()
        fun onScrolledToBottom()
        fun onListItemClicked(post: ListAdapterModel.Post)
        fun onHotFilterSelected()
        fun onNewFilterSelected()
        fun onTopFilterSelected(type: TopFilterType)
        fun onMoreMenuButtonClicked()
        fun onSettingsButtonClicked()
    }

    interface ViewModel {
        fun setView(view: View)
    }
}

internal data class State(
    val adapterData: List<ListAdapterModel>,
    val fetchAfter: String?,
    val errorMessage: Event<String?>?,
    val isLoading: Boolean,
    val filterMenuIsVisible: Event<Boolean>,
    val filterType: Event<FilterType>,
) {
    companion object {
        fun default() = State(
            adapterData = listOf(ListAdapterModel.Loading),
            fetchAfter = null,
            errorMessage = null,
            isLoading = true,
            filterMenuIsVisible = Event(false),
            filterType = Event(FilterType.Hot)
        )
    }

    override fun toString(): String {
        return "AdapterList: ${adapterData.size}, isErrored: ${errorMessage?.peekContent()}," +
            " isLoading: $isLoading, filterMenuVisible: ${filterMenuIsVisible.peekContent()}," +
            " filterType: ${filterType.peekContent().javaClass.simpleName}"
    }
}

internal sealed class Action {
    data class DataChanged(val postResponse: DataState<PostResponse>) : Action() {
        override fun toString(): String {
            return if (postResponse is DataState.Data) {
                "DataChanged.Data with size: ${postResponse()?.postData?.size}"
            } else super.toString()
        }
    }
    object HotFilterSelected : Action()
    object NewFilterSelected : Action()
    data class TopFilterSelected(val topPeriod: TopFilterType) : Action()
    object MoreFilterButtonClicked : Action()
    object SwipedToRefresh: Action()
    object FragmentCreated: Action()
}

