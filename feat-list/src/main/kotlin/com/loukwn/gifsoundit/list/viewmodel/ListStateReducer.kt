package com.loukwn.gifsoundit.list.viewmodel

import com.loukwn.gifsoundit.domain.DataState
import com.loukwn.gifsoundit.domain.Event
import com.loukwn.gifsoundit.list.Action
import com.loukwn.gifsoundit.list.State
import com.loukwn.gifsoundit.list.view.adapter.ListAdapterModel
import com.loukwn.gifsoundit.list.view.adapter.toAdapterModel
import javax.inject.Inject

internal class ListStateReducer @Inject constructor() {
    fun reduce(oldState: State, action: Action): State {
        return when (action) {
            is Action.DataChanged -> {
                when (action.postResponse) {
                    is DataState.Loading -> oldState.copy(errorMessage = null)
                    is DataState.Data -> {
                        action.postResponse()?.let { response ->
                            val newData =
                                ArrayList<ListAdapterModel>(response.postData.map { it.toAdapterModel() }).apply {
                                    if (response.canFetchMore) {
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
            Action.ArrowButtonClicked -> oldState.copy(optionsLayoutIsOpen = !oldState.optionsLayoutIsOpen)
            Action.SwipedToRefresh -> oldState.copy(
                adapterData = emptyList(),
                isLoading = true
            )
            Action.FragmentCreated -> oldState
            Action.OverlayClicked -> oldState.copy(optionsLayoutIsOpen = false)
            is Action.SaveButtonClicked -> {
                if (action.filterType != oldState.filterType || action.sourceType != oldState.sourceType) {
                    oldState.copy(
                        adapterData = emptyList(),
                        isLoading = true,
                        optionsLayoutIsOpen = false,
                        filterType = action.filterType,
                        sourceType = action.sourceType,
                    )
                } else {
                    oldState.copy(optionsLayoutIsOpen = false)
                }
            }
            Action.OnBackPressed -> oldState.copy(optionsLayoutIsOpen = false)
        }
    }
}
