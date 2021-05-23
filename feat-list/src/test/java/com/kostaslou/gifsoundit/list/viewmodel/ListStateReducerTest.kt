package com.kostaslou.gifsoundit.list.viewmodel

import com.kostaslou.gifsoundit.common.util.DataState
import com.kostaslou.gifsoundit.common.util.Event
import com.kostaslou.gifsoundit.list.Action
import com.kostaslou.gifsoundit.list.FilterType
import com.kostaslou.gifsoundit.list.SourceType
import com.kostaslou.gifsoundit.list.State
import com.kostaslou.gifsoundit.list.view.adapter.ListAdapterModel
import com.loukwn.postdata.model.domain.PostModel
import com.loukwn.postdata.model.domain.PostResponse
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

internal class ListStateReducerTest {
    private val sut = ListStateReducer()

    @Test
    fun `GIVEN action is DataChanged_Loading AND current list is empty WHEN map THEN add a loading element and remove error message`() {
        val oldState = State.default().copy(adapterData = listOf())
        val action = Action.DataChanged(postResponse = DataState.Loading())

        val newState = sut.reduce(oldState, action)

        assertEquals(
            newState,
            oldState.copy(
                adapterData = listOf(),
                errorMessage = null
            )
        )
    }

    @Test
    fun `GIVEN action is DataChanged_Loading AND current list is not empty WHEN map THEN just remove the error message`() {
        val oldState = State.default().copy(adapterData = listOf(mockk()))
        val action = Action.DataChanged(postResponse = DataState.Loading())

        val newState = sut.reduce(oldState, action)

        assertEquals(
            newState,
            oldState.copy(
                errorMessage = null
            )
        )
    }

    @Test
    fun `GIVEN action is DataChanged_Data AND response says that we can load more WHEN map THEN add a loading item at the end`() {
        val oldState = State.default().copy(adapterData = listOf(ListAdapterModel.Loading))
        val postData = arrayListOf<PostModel>().apply {
            for (i in 0..5) {
                this.add(mockk(relaxed = true))
            }
        }
        val action = Action.DataChanged(
            postResponse = DataState.Data(
                PostResponse(
                    postData = postData,
                    canFetchMore = true,
                    after = ""
                )
            )
        )

        val newState = sut.reduce(oldState, action)

        assertEquals(
            postData.size + 1,
            newState.adapterData.size
        )
    }

    @Test
    fun `GIVEN action is DataChanged_Data AND response says that we cannot load more WHEN map THEN do not add a loading item at the end`() {
        val oldState = State.default().copy(adapterData = listOf(ListAdapterModel.Loading))
        val postData = arrayListOf<PostModel>().apply {
            for (i in 0..5) {
                this.add(mockk(relaxed = true))
            }
        }
        val action = Action.DataChanged(
            postResponse = DataState.Data(
                PostResponse(
                    postData = postData,
                    canFetchMore = false,
                    after = ""
                )
            )
        )

        val newState = sut.reduce(oldState, action)

        assertEquals(
            postData.size,
            newState.adapterData.size
        )
    }

    @Test
    fun `GIVEN action is DataChanged_Error WHEN map THEN send error message event`() {
        val oldState = State.default().copy(adapterData = listOf(mockk()))
        val action = Action.DataChanged(
            postResponse = DataState.Error(
                formattedError = "error",
                error = mockk()
            )
        )

        val newState = sut.reduce(oldState, action)

        assertEquals(
            newState,
            oldState.copy(
                errorMessage = Event("error"),
                isLoading = false
            )
        )
    }

    @Test
    fun `GIVEN action is SaveButtonClicked AND filterType is different WHEN map THEN close options layout AND switch to that`() {
        val oldState = State.default().copy(optionsLayoutIsOpen = true, filterType = FilterType.Hot)
        val action =
            Action.SaveButtonClicked(sourceType = oldState.sourceType, filterType = FilterType.New)

        val newState = sut.reduce(oldState, action)

        assertEquals(false, newState.optionsLayoutIsOpen)
        assertEquals(FilterType.New, newState.filterType)
    }

    @Test
    fun `GIVEN action is SaveButtonClicked AND sourceType is different WHEN map THEN close options layout AND switch to that`() {
        val oldState =
            State.default().copy(optionsLayoutIsOpen = true, sourceType = SourceType.GifSound)
        val action = Action.SaveButtonClicked(
            filterType = oldState.filterType,
            sourceType = SourceType.MusicGifStation
        )

        val newState = sut.reduce(oldState, action)

        assertEquals(false, newState.optionsLayoutIsOpen)
        assertEquals(SourceType.MusicGifStation, newState.sourceType)
    }

    @Test
    fun `GIVEN action is SaveButtonClicked AND sourceType and filterType are the same WHEN map THEN just close options layout`() {
        val oldState =
            State.default().copy(optionsLayoutIsOpen = true, sourceType = SourceType.GifSound)
        val action = Action.SaveButtonClicked(
            filterType = oldState.filterType,
            sourceType = oldState.sourceType
        )

        val newState = sut.reduce(oldState, action)

        assertEquals(oldState.copy(optionsLayoutIsOpen = false), newState)
    }

    @Test
    fun `GIVEN action is OverlayClicked WHEN map THEN reverse options layout state`() {
        val oldState = State.default().copy(optionsLayoutIsOpen = true)
        val action = Action.OverlayClicked

        val newState = sut.reduce(oldState, action)

        assertEquals(false, newState.optionsLayoutIsOpen)
    }

    @Test
    fun `GIVEN action is ArrowButtonClicked WHEN map THEN reverse options layout state`() {
        val oldState = State.default().copy(optionsLayoutIsOpen = true)
        val action = Action.ArrowButtonClicked

        val newState = sut.reduce(oldState, action)

        assertEquals(false, newState.optionsLayoutIsOpen)
    }

    @Test
    fun `GIVEN action is SwipedToRefresh WHEN map THEN update state accordingly`() {
        val oldState = State.default()
        val action = Action.SwipedToRefresh

        val newState = sut.reduce(oldState, action)

        assertEquals(
            newState,
            oldState.copy(
                adapterData = emptyList(),
                isLoading = true
            )
        )
    }

    @Test
    fun `GIVEN action is OnBackPressed WHEN map THEN close options layout`() {
        val oldState = State.default().copy(optionsLayoutIsOpen = true)
        val action = Action.OnBackPressed

        val newState = sut.reduce(oldState, action)

        assertEquals(
            false,
            newState.optionsLayoutIsOpen
        )
    }
}
