package com.loukwn.gifsoundit.list.viewmodel

import com.loukwn.gifsoundit.common.util.Event
import com.loukwn.gifsoundit.list.ListContract
import com.loukwn.gifsoundit.list.State
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

internal class ListViewPresenterTest {
    private val sut = ListViewPresenter(mockk(relaxed = true))

    @Test
    fun `GIVEN errorMessage AND not handled WHEN updating the view THEN show it`() {
        val view = mockk<ListContract.View>(relaxUnitFun = true)
        val state = State.default().copy(errorMessage = Event("error"))

        sut.updateView(view, state)

        verify(exactly = 1) { view.showErrorToast(any()) }
    }

    @Test
    fun `GIVEN errorMessage AND handled WHEN updating the view THEN show it`() {
        val view = mockk<ListContract.View>(relaxUnitFun = true)
        val event = Event("error").apply { getContentIfNotHandled() }
        val state = State.default().copy(errorMessage = event)

        sut.updateView(view, state)

        verify(exactly = 0) { view.showErrorToast(any()) }
    }

    @Test
    fun `GIVEN isLoading is true WHEN updating the view THEN do not allow swipe to refresh and scroll to bottom loading`() {
        val view = mockk<ListContract.View>(relaxUnitFun = true)
        val state = State.default().copy(isLoading = true)

        sut.updateView(view, state)

        verify(exactly = 1) { view.allowOrNotSwipeToRefresh(allow = false) }
        verify(exactly = 1) { view.allowOrNotScrollToBottomLoading(allow = false) }
    }

    @Test
    fun `GIVEN isLoading is false WHEN updating the view THEN do not allow swipe to refresh and scroll to bottom loading`() {
        val view = mockk<ListContract.View>(relaxUnitFun = true)
        val state = State.default().copy(isLoading = false)

        sut.updateView(view, state)

        verify(exactly = 1) { view.allowOrNotSwipeToRefresh(allow = true) }
        verify(exactly = 1) { view.allowOrNotScrollToBottomLoading(allow = true) }
    }

    @Test
    fun `WHEN updating the view THEN update the list`() {
        val view = mockk<ListContract.View>(relaxUnitFun = true)
        val state = State.default()

        sut.updateView(view, state)

        verify(exactly = 1) { view.showList(any()) }
    }
}
