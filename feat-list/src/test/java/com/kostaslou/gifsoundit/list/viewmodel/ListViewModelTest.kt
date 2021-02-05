package com.kostaslou.gifsoundit.list.viewmodel

import com.kostaslou.gifsoundit.common.util.Event
import com.kostaslou.gifsoundit.list.Action
import com.kostaslou.gifsoundit.list.ListContract
import com.kostaslou.gifsoundit.list.State
import com.loukwn.navigation.Navigator
import com.loukwn.postdata.FilterType
import com.loukwn.postdata.PostRepository
import com.loukwn.postdata.TopFilterType
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

internal class ListViewModelTest {
    private lateinit var sut: ListViewModel

    @MockK
    lateinit var repository: PostRepository

    @MockK
    lateinit var navigator: Navigator

    @MockK
    lateinit var listStateReducer: ListStateReducer

    @MockK
    lateinit var listViewPresenter: ListViewPresenter

    private val trampolineScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        sut = ListViewModel(
            repository,
            navigator,
            listStateReducer,
            listViewPresenter,
            trampolineScheduler,
            trampolineScheduler
        )
    }

    @Test
    fun `GIVEN view is set WHEN onStart THEN set view listener to this`() {
        val view = mockk<ListContract.View>(relaxed = true)
        sut.setView(view)

        sut.doOnStart()

        verify(exactly = 1) { view.setListener(sut) }
    }

    @Test
    fun `GIVEN view is set WHEN onStop THEN remove view listener`() {
        val view = mockk<ListContract.View>(relaxed = true)
        sut.setView(view)

        sut.doOnStop()

        verify(exactly = 1) { view.removeListener(sut) }
    }

    @Test
    fun `GIVEN view is set WHEN onResume THEN refreshTokenIfNeeded`() {
        val view = mockk<ListContract.View>(relaxed = true)
        sut.setView(view)

        sut.doOnResume()

        verify(exactly = 1) { repository.refreshAuthTokenIfNeeded() }
    }

    @Test
    fun `GIVEN view is set WHEN onCreate THEN update view for first time`() {
        val view = mockk<ListContract.View>(relaxed = true)
        sut.setView(view)

        sut.doOnCreate()

        verify(exactly = 1) { listStateReducer.map(any(), Action.FragmentCreated) }
        verify(exactly = 1) { listViewPresenter.updateView(any(), any()) }
    }

    @Test
    fun `WHEN onSettingsButtonClicked THEN navigateToSettings`() {
        sut.onSettingsButtonClicked()

        verify(exactly = 1) { navigator.navigateToSettings() }
    }

    @Test
    fun `WHEN onListItemClicked THEN navigateToOpenGS`() {
        sut.onListItemClicked(mockk(relaxed = true))

        verify(exactly = 1) { navigator.navigateToOpenGS(any(), any()) }
    }

    @Test
    fun `WHEN onMoreMenuButtonClicked THEN make sure reducer gets the action`() {
        sut.onMoreMenuButtonClicked()

        verify(exactly = 1) { listStateReducer.map(any(), Action.MoreFilterButtonClicked) }
    }

    @Test
    fun `WHEN onSwipeToRefresh THEN make sure reducer gets the action AND new posts are requested`() {
        every { listStateReducer.map(any(), any()) } returns State.default()
            .copy(filterType = Event(FilterType.New))

        sut.onSwipeToRefresh()

        verify(exactly = 1) { listStateReducer.map(any(), Action.SwipedToRefresh) }
        verify(exactly = 1) { repository.getPosts(FilterType.New, "") }
    }

    @Test
    fun `GIVEN fetchAfter is null WHEN onScrolledToBottom THEN do not get any more posts from repo`() {
        every { listStateReducer.map(any(), any()) } returns State.default()
            .copy(filterType = Event(FilterType.New))

        sut.onMoreMenuButtonClicked() // this just sets the state that we need for the actual test
        sut.onScrolledToBottom()

        verify(exactly = 0) { repository.getPosts(eq(FilterType.New), any()) }
    }

    @Test
    fun `GIVEN fetchAfter is not null WHEN onScrolledToBottom THEN do not get any more posts from repo`() {
        every { listStateReducer.map(any(), any()) } returns State.default()
            .copy(filterType = Event(FilterType.New), fetchAfter = "1")

        sut.onMoreMenuButtonClicked() // this just sets the state that we need for the actual test
        sut.onScrolledToBottom()

        verify(exactly = 1) { repository.getPosts(FilterType.New, "1") }
    }

    @Test
    fun `GIVEN filterType is not hot WHEN onHotFilterSelected THEN get more posts from repo`() {
        every { listStateReducer.map(any(), any()) } returns State.default()
            .copy(filterType = Event(FilterType.New))

        sut.onMoreMenuButtonClicked() // this just sets the state that we need for the actual test
        sut.onHotFilterSelected()

        verify(exactly = 2) { repository.getPosts(FilterType.Hot, any()) } // 1 is made during init()
        verify(exactly = 1) { listStateReducer.map(any(), Action.HotFilterSelected) }
    }

    @Test
    fun `GIVEN filterType is hot WHEN onHotFilterSelected THEN do not get more posts from repo`() {
        sut.onHotFilterSelected()

        verify(exactly = 1) { repository.getPosts(FilterType.Hot, any()) } // just the 1 time during init()
        verify(exactly = 0) { listStateReducer.map(any(), Action.HotFilterSelected) }
    }

    @Test
    fun `GIVEN filterType is not new WHEN onNewFilterSelected THEN get more posts from repo`() {
        sut.onNewFilterSelected()

        verify(exactly = 1) { repository.getPosts(FilterType.New, any()) }
        verify(exactly = 1) { listStateReducer.map(any(), Action.NewFilterSelected) }
    }

    @Test
    fun `GIVEN filterType is not new WHEN onNewFilterSelected THEN do not get more posts from repo`() {
        every { listStateReducer.map(any(), any()) } returns State.default()
            .copy(filterType = Event(FilterType.New))

        sut.onMoreMenuButtonClicked() // this just sets the state that we need for the actual test
        sut.onNewFilterSelected()

        verify(exactly = 0) { repository.getPosts(FilterType.New, any()) }
        verify(exactly = 0) { listStateReducer.map(any(), Action.NewFilterSelected) }
    }

    @Test
    fun `GIVEN filterType is not top WHEN onTopFilterSelected THEN get more posts from repo`() {
        sut.onTopFilterSelected(TopFilterType.ALL)

        verify(exactly = 1) { repository.getPosts(FilterType.Top(TopFilterType.ALL), any()) }
        verify(exactly = 1) { listStateReducer.map(any(), Action.TopFilterSelected(TopFilterType.ALL)) }
    }

    @Test
    fun `GIVEN filterType is top but of different type to the one requested WHEN onTopFilterSelected THEN get more posts from repo`() {
        every { listStateReducer.map(any(), any()) } returns State.default()
            .copy(filterType = Event(FilterType.Top(TopFilterType.ALL)))

        sut.onMoreMenuButtonClicked() // this just sets the state that we need for the actual test
        sut.onTopFilterSelected(TopFilterType.YEAR)

        verify(exactly = 1) { repository.getPosts(FilterType.Top(TopFilterType.YEAR), any()) }
        verify(exactly = 1) { listStateReducer.map(any(), Action.TopFilterSelected(TopFilterType.YEAR)) }
    }

    @Test
    fun `GIVEN filterType is top but of same type to the one requested WHEN onTopFilterSelected THEN do not get more posts from repo`() {
        every { listStateReducer.map(any(), any()) } returns State.default()
            .copy(filterType = Event(FilterType.Top(TopFilterType.ALL)))

        sut.onMoreMenuButtonClicked() // this just sets the state that we need for the actual test
        sut.onTopFilterSelected(TopFilterType.ALL)

        verify(exactly = 0) { repository.getPosts(FilterType.Top(TopFilterType.ALL), any()) }
        verify(exactly = 0) { listStateReducer.map(any(), Action.TopFilterSelected(TopFilterType.ALL)) }
    }

    @Test
    fun `WHEN viewmodel is cleared THEN nothing weird happens`() {
        sut.onCleared()
    }
}
