package com.kostaslou.gifsoundit.list.viewmodel

import com.kostaslou.gifsoundit.common.util.DataState
import com.kostaslou.gifsoundit.common.util.Event
import com.kostaslou.gifsoundit.list.Action
import com.kostaslou.gifsoundit.list.State
import com.kostaslou.gifsoundit.list.view.adapter.ListAdapterModel
import com.kostaslou.gifsoundit.list.view.adapter.toAdapterModel
import com.loukwn.postdata.FilterType
import com.loukwn.postdata.RedditConstants
import javax.inject.Inject

internal class ListStateReducer @Inject constructor() {
    fun map(oldState: State, action: Action): State {
        return when (action) {
            is Action.DataChanged -> {
                when (action.postResponse) {
                    is DataState.Loading -> oldState.copy(errorMessage = null)
                    is DataState.Data -> {
                        action.postResponse()?.let { response ->
                            val newData =
                                ArrayList<ListAdapterModel>(response.postData.map { it.toAdapterModel() }).apply {
                                    if (this.size == RedditConstants.NUM_OF_POSTS_PER_REQUEST) {
                                        add(ListAdapterModel.Loading)
                                    }
                                }

                            val finalData = oldState.adapterData
                                .minus(ListAdapterModel.Loading)
                                .plus(newData)

                            oldState.copy(
                                adapterData = finalData,
                                errorMessage = null,
                                isLoading = false,
                                fetchAfter = response.after
                            )
                        } ?: oldState
                    }
                    is DataState.Error -> oldState.copy(
                        errorMessage = Event(action.postResponse.formattedError),
                        isLoading = false
                    )
                }
            }
            Action.HotFilterSelected -> oldState.copy(
                adapterData = emptyList(),
                filterType = Event(FilterType.Hot),
                isLoading = true,
                errorMessage = null,
                filterMenuIsVisible = Event(false)
            )
            Action.NewFilterSelected -> oldState.copy(
                adapterData = emptyList(),
                filterType = Event(FilterType.New),
                isLoading = true,
                errorMessage = null,
                filterMenuIsVisible = Event(false)
            )
            is Action.TopFilterSelected -> oldState.copy(
                adapterData = emptyList(),
                filterType = Event(FilterType.Top(action.topPeriod)),
                isLoading = true,
                errorMessage = null,
                filterMenuIsVisible = Event(false)
            )
            Action.MoreFilterButtonClicked -> oldState.copy(
                filterMenuIsVisible = Event(!oldState.filterMenuIsVisible.peekContent())
            )
            Action.SwipedToRefresh -> oldState.copy(
                adapterData = emptyList(),
                isLoading = true
            )
            Action.FragmentCreated -> oldState.copy(
                filterMenuIsVisible = Event(oldState.filterMenuIsVisible.peekContent()),
                filterType = Event(oldState.filterType.peekContent())
            )
        }
    }
}
