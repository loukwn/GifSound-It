package com.kostaslou.gifsoundit.list.viewmodel

import com.kostaslou.gifsoundit.common.util.Event
import com.kostaslou.gifsoundit.list.ListContract
import com.kostaslou.gifsoundit.list.State
import com.loukwn.postdata.FilterTypeDTO
import com.loukwn.postdata.TopFilterTypeDTO
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

internal class ListViewPresenterTest {
    private val sut = ListViewPresenter()

    @Test
    fun `GIVEN filterMenuIsVisible true event AND not handled WHEN updating the view THEN show filter menu`() {
        val view = mockk<ListContract.View>(relaxUnitFun = true)
        val state = State.default().copy(filterMenuIsVisible = Event(true))

        sut.updateView(view, state)

        verify(exactly = 1) { view.showFilterMenu() }
    }

    @Test
    fun `GIVEN filterMenuIsVisible false event AND not handled WHEN updating the view THEN hide filter menu`() {
        val view = mockk<ListContract.View>(relaxUnitFun = true)
        val state = State.default().copy(filterMenuIsVisible = Event(false))

        sut.updateView(view, state)

        verify(exactly = 1) { view.hideFilterMenu() }
    }

    @Test
    fun `GIVEN filterMenuIsVisible true event AND handled WHEN updating the view THEN do nothing`() {
        val view = mockk<ListContract.View>(relaxUnitFun = true)
        val event = Event(true).apply { getContentIfNotHandled() }
        val state = State.default().copy(filterMenuIsVisible = event)

        sut.updateView(view, state)

        verify(exactly = 0) { view.showFilterMenu() }
        verify(exactly = 0) { view.hideFilterMenu() }
    }

    @Test
    fun `GIVEN filterType hot AND not handled WHEN updating the view THEN set filter menu to hot`() {
        val view = mockk<ListContract.View>(relaxUnitFun = true)
        val state = State.default().copy(filterType = Event(FilterTypeDTO.Hot))

        sut.updateView(view, state)

        verify(exactly = 1) { view.setFilterMenuToHot() }
    }

    @Test
    fun `GIVEN filterType top AND not handled WHEN updating the view THEN set filter menu to top`() {
        val view = mockk<ListContract.View>(relaxUnitFun = true)
        val state = State.default().copy(filterType = Event(FilterTypeDTO.Top(TopFilterTypeDTO.ALL)))

        sut.updateView(view, state)

        verify(exactly = 1) { view.setFilterMenuToTop() }
    }

    @Test
    fun `GIVEN filterType new AND not handled WHEN updating the view THEN set filter menu to new`() {
        val view = mockk<ListContract.View>(relaxUnitFun = true)
        val state = State.default().copy(filterType = Event(FilterTypeDTO.New))

        sut.updateView(view, state)

        verify(exactly = 1) { view.setFilterMenuToNew() }
    }

    @Test
    fun `GIVEN filterType new AND handled WHEN updating the view THEN do nothing`() {
        val view = mockk<ListContract.View>(relaxUnitFun = true)
        val event = Event(FilterTypeDTO.New).apply { getContentIfNotHandled() }
        val state = State.default().copy(filterType = event)

        sut.updateView(view, state)

        verify(exactly = 0) { view.setFilterMenuToHot() }
        verify(exactly = 0) { view.setFilterMenuToTop() }
        verify(exactly = 0) { view.setFilterMenuToNew() }
    }

    @Test
    fun `GIVEN errorMessage AND not handled WHEN updating the view THEN show it`() {
        val view = mockk<ListContract.View>(relaxUnitFun = true)
        val state = State.default().copy(errorMessage = Event("error"))

        sut.updateView(view, state)

        verify(exactly = 1) { view.showErrorToast("error") }
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
