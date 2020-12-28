package com.kostaslou.gifsoundit.list.viewmodel

import com.kostaslou.gifsoundit.common.util.DataState
import com.kostaslou.gifsoundit.list.view.adapter.ListAdapterModel
import com.kostaslou.gifsoundit.list.view.adapter.toAdapterModel
import com.loukwn.postdata.FilterType
import com.loukwn.postdata.RedditConstants

object ListStateReducer {
    fun map(oldState: ListViewModel.State, action: ListViewModel.Action): ListViewModel.State {
        return when (action) {
            is ListViewModel.Action.DataChanged -> {
                when (action.postResponse) {
                    is DataState.Loading -> {
                        if (oldState.adapterData.isEmpty()) {
                            oldState.copy(
                                adapterData = listOf(ListAdapterModel.Loading),
                                isErrored = false
                            )
                        } else {
                            oldState.copy(isErrored = false)
                        }
                    }
                    is DataState.Data -> {
                        action.postResponse()?.let { response ->
                            var reachedTheEnd = false
                            val newData = response.postData.map { it.toAdapterModel() }.apply {
                                if (this.size == RedditConstants.NUM_OF_POSTS_PER_REQUEST) {
                                    plus(ListAdapterModel.Loading)
                                } else {
                                    reachedTheEnd = true
                                }
                            }

                            val finalData = oldState.adapterData
                                .minus(ListAdapterModel.Loading)
                                .plus(newData)

                            oldState.copy(
                                adapterData = finalData,
                                isErrored = false,
                                isLoading = false,
                                reachedTheEnd = reachedTheEnd,
                                fetchAfter = response.after
                            )
                        } ?: oldState
                    }
                    is DataState.Error -> oldState.copy(
                        adapterData = emptyList(),
                        isErrored = true,
                        isLoading = false
                    )
                }
            }
            ListViewModel.Action.HotFilterSelected -> oldState.copy(
                adapterData = listOf(ListAdapterModel.Loading),
                filterType = FilterType.Hot,
                isLoading = true,
                isErrored = false,
                filterMenuIsVisible = false
            )
            ListViewModel.Action.NewFilterSelected -> oldState.copy(
                adapterData = listOf(ListAdapterModel.Loading),
                filterType = FilterType.New,
                isLoading = true,
                isErrored = false,
                filterMenuIsVisible = false
            )
            is ListViewModel.Action.TopFilterSelected -> oldState.copy(
                adapterData = listOf(ListAdapterModel.Loading),
                filterType = FilterType.Top(action.topPeriod),
                isLoading = true,
                isErrored = false,
                filterMenuIsVisible = false
            )
            ListViewModel.Action.MoreFilterButtonClicked -> oldState.copy(
                filterMenuIsVisible = !oldState.filterMenuIsVisible
            )
            ListViewModel.Action.SwipedToRefresh -> oldState.copy(
                adapterData = listOf(ListAdapterModel.Loading),
                isLoading = true,
                reachedTheEnd = false
            )
        }
    }
}
