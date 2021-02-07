package com.kostaslou.gifsoundit.list.viewmodel

import com.kostaslou.gifsoundit.common.util.DataState
import com.kostaslou.gifsoundit.common.util.Event
import com.kostaslou.gifsoundit.list.Action
import com.kostaslou.gifsoundit.list.State
import com.kostaslou.gifsoundit.list.view.adapter.ListAdapterModel
import com.loukwn.postdata.FilterType
import com.loukwn.postdata.RedditConstants
import com.loukwn.postdata.TopFilterType
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

        val newState = sut.map(oldState, action)

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

        val newState = sut.map(oldState, action)

        assertEquals(
            newState,
            oldState.copy(
                errorMessage = null
            )
        )
    }

    @Test
    fun `GIVEN action is DataChanged_Data AND response is as big as the max num of posts per request WHEN map THEN add a loading item at the end`() {
        val oldState = State.default().copy(adapterData = listOf(ListAdapterModel.Loading))
        val postData = arrayListOf<PostModel>().apply {
            for (i in 0 until RedditConstants.NUM_OF_POSTS_PER_REQUEST) {
                this.add(mockk(relaxed = true))
            }
        }
        val action = Action.DataChanged(postResponse = DataState.Data(PostResponse(postData, "")))

        val newState = sut.map(oldState, action)

        assertEquals(
            RedditConstants.NUM_OF_POSTS_PER_REQUEST + 1,
            newState.adapterData.size
        )
    }

    @Test
    fun `GIVEN action is DataChanged_Data AND response is less than the max num of posts per request WHEN map THEN do not add a loading item at the end`() {
        val oldState = State.default().copy(adapterData = listOf(ListAdapterModel.Loading))
        val postData = arrayListOf<PostModel>().apply {
            for (i in 0 until RedditConstants.NUM_OF_POSTS_PER_REQUEST - 1) {
                this.add(mockk(relaxed = true))
            }
        }
        val action = Action.DataChanged(postResponse = DataState.Data(PostResponse(postData, "")))

        val newState = sut.map(oldState, action)

        assertEquals(
            RedditConstants.NUM_OF_POSTS_PER_REQUEST - 1,
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

        val newState = sut.map(oldState, action)

        assertEquals(
            newState,
            oldState.copy(
                errorMessage = Event("error"),
                isLoading = false
            )
        )
    }

    @Test
    fun `GIVEN action is HotFilterSelected WHEN map THEN update the state accordingly`() {
        val oldState = State.default()
        val action = Action.HotFilterSelected

        val newState = sut.map(oldState, action)

        assertEquals(
            newState,
            oldState.copy(
                adapterData = emptyList(),
                filterType = Event(FilterType.Hot),
                isLoading = true,
                errorMessage = null,
                filterMenuIsVisible = Event(false)
            )
        )
    }

    @Test
    fun `GIVEN action is NewFilterSelected WHEN map THEN update the state accordingly`() {
        val oldState = State.default()
        val action = Action.NewFilterSelected

        val newState = sut.map(oldState, action)

        assertEquals(
            newState,
            oldState.copy(
                adapterData = emptyList(),
                filterType = Event(FilterType.New),
                isLoading = true,
                errorMessage = null,
                filterMenuIsVisible = Event(false)
            )
        )
    }

    @Test
    fun `GIVEN action is TopFilterSelected WHEN map THEN update the state accordingly`() {
        val oldState = State.default()
        val action = Action.TopFilterSelected(topPeriod = TopFilterType.ALL)

        val newState = sut.map(oldState, action)

        assertEquals(
            newState,
            oldState.copy(
                adapterData = emptyList(),
                filterType = Event(FilterType.Top(action.topPeriod)),
                isLoading = true,
                errorMessage = null,
                filterMenuIsVisible = Event(false)
            )
        )
    }

    @Test
    fun `GIVEN action is MoreFilterButtonClicked WHEN map THEN toggle menu visibility with an event`() {
        val oldState = State.default().copy(filterMenuIsVisible = Event(true))
        val action = Action.MoreFilterButtonClicked

        val newState = sut.map(oldState, action)

        assertEquals(
            newState,
            oldState.copy(
                filterMenuIsVisible = Event(false)
            )
        )
    }

    @Test
    fun `GIVEN action is SwipedToRefresh WHEN map THEN update state accordingly`() {
        val oldState = State.default()
        val action = Action.SwipedToRefresh

        val newState = sut.map(oldState, action)

        assertEquals(
            newState,
            oldState.copy(
                adapterData = emptyList(),
                isLoading = true
            )
        )
    }

    @Test
    fun `GIVEN action is FragmentCreated WHEN map THEN update the filter menu state accordingly`() {
        val oldState = State.default()
            .copy(filterMenuIsVisible = Event(true), filterType = Event(FilterType.New))
        val action = Action.FragmentCreated

        val newState = sut.map(oldState, action)

        assertEquals(
            newState,
            oldState.copy(
                filterMenuIsVisible = Event(true),
                filterType = Event(FilterType.New)
            )
        )
    }
}
