package com.kostaslou.gifsoundit.list.viewmodel

import com.kostaslou.gifsoundit.list.Action
import com.kostaslou.gifsoundit.list.FilterType
import com.kostaslou.gifsoundit.list.ListContract
import com.kostaslou.gifsoundit.list.SourceType
import com.kostaslou.gifsoundit.list.State
import com.kostaslou.gifsoundit.list.util.toDTO
import com.loukwn.navigation.Navigator
import com.loukwn.postdata.PostRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

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

    private val testScheduler = TestScheduler()

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        sut = ListViewModel(
            repository = repository,
            navigator = navigator,
            listStateReducer = listStateReducer,
            listViewPresenter = listViewPresenter,
            ioScheduler = trampolineScheduler,
            uiScheduler = trampolineScheduler,
            computationScheduler = testScheduler,
        )
    }

    @Test
    fun `WHEN fragment is recreated THEN send an event to set the state again`() {
        sut.doOnCreate()
        sut.doOnCreate() // recreation

        verify(exactly = 1) { listStateReducer.reduce(any(), Action.FragmentCreated) }
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
    fun `WHEN onSwipeToRefresh THEN make sure reducer gets the action AND new posts are requested`() {
        every { listStateReducer.reduce(any(), any()) } returns State.default()
            .copy(filterType = FilterType.New)

        sut.onSwipeToRefresh()

        verify(exactly = 1) { listStateReducer.reduce(any(), Action.SwipedToRefresh) }
        verify(exactly = 1) { repository.getPosts(any(), FilterType.New.toDTO(), "") }
    }

    @Test
    fun `GIVEN fetchAfter is not null WHEN onScrolledToBottom THEN new posts are requested`() {
        every { listStateReducer.reduce(any(), any()) } returns State.default()
            .copy(fetchAfter = "1")

        sut.onArrowButtonClicked() // This just sets the state
        sut.onScrolledToBottom()

        verify(exactly = 1) { repository.getPosts(any(), any(), "1") }
    }

    @Test
    fun `GIVEN fetchAfter is null WHEN onScrolledToBottom THEN new posts are requested`() {
        every { listStateReducer.reduce(any(), any()) } returns State.default()
            .copy(fetchAfter = null)

        sut.onArrowButtonClicked() // This just sets the state
        sut.onScrolledToBottom()

        verify(exactly = 0) { repository.getPosts(any(), any(), "1") }
    }

    @Test
    fun `WHEN onListItemClicked THEN navigateToOpenGS`() {
        sut.onListItemClicked(mockk(relaxed = true), mockk())

        verify(exactly = 1) { navigator.navigateToOpenGS(any(), any(), any()) }
    }

    @Test
    fun `WHEN onSaveButtonClicked AND filterType is different THEN getPosts AND send event to reducer`() {
        every { listStateReducer.reduce(any(), any()) } returns State.default()
            .copy(filterType = FilterType.New)

        sut.onArrowButtonClicked() // This just sets the state
        sut.onSaveButtonClicked(
            selectedSourceType = SourceType.GifSound,
            selectedFilterType = FilterType.TopAll
        )

        verify(exactly = 1) {
            listStateReducer.reduce(
                any(),
                Action.SaveButtonClicked(SourceType.GifSound, FilterType.TopAll),
            )
        }
        verify(exactly = 1) {
            repository.getPosts(
                sourceType = any(),
                filterType = FilterType.TopAll.toDTO(),
                after = "",
            )
        }
    }

    @Test
    fun `WHEN onSaveButtonClicked AND sourceType is different THEN getPosts AND send event to reducer`() {
        every { listStateReducer.reduce(any(), any()) } returns State.default()
            .copy(sourceType = SourceType.GifSound)

        sut.onArrowButtonClicked() // This just sets the state
        sut.onSaveButtonClicked(
            selectedSourceType = SourceType.MusicGifStation,
            selectedFilterType = FilterType.Hot,
        )

        verify(exactly = 1) {
            listStateReducer.reduce(
                any(),
                Action.SaveButtonClicked(SourceType.MusicGifStation, FilterType.Hot)
            )
        }
        verify(exactly = 1) {
            repository.getPosts(
                sourceType = SourceType.MusicGifStation.toDTO(),
                filterType = any(),
                after = "",
            )
        }
    }

    @Test
    fun `WHEN onSaveButtonClicked AND sourceType and filterType are the same THEN just send event to reducer`() {
        every { listStateReducer.reduce(any(), any()) } returns State.default()
            .copy(sourceType = SourceType.AnimeGifSound, filterType = FilterType.TopAll)

        sut.onArrowButtonClicked() // This just sets the state
        sut.onSaveButtonClicked(
            selectedSourceType = SourceType.AnimeGifSound,
            selectedFilterType = FilterType.TopAll
        )

        verify(exactly = 1) {
            listStateReducer.reduce(
                any(),
                Action.SaveButtonClicked(SourceType.AnimeGifSound, FilterType.TopAll),
            )
        }
        verify(exactly = 0) {
            repository.getPosts(
                sourceType = SourceType.AnimeGifSound.toDTO(),
                filterType = FilterType.TopAll.toDTO(),
                after = any(),
            )
        }
    }

    @Test
    fun `WHEN ArrowButtonClicked THEN make sure reducer gets the action`() {
        sut.onArrowButtonClicked()

        verify(exactly = 1) { listStateReducer.reduce(any(), Action.ArrowButtonClicked) }
    }

    @Test
    fun `WHEN onSettingsButtonClicked THEN navigateToSettings`() {
        sut.onSettingsButtonClicked()

        verify(exactly = 1) { navigator.navigateToSettings() }
    }

    @Test
    fun `WHEN OverlayClicked THEN make sure reducer gets the action`() {
        sut.onOverlayClicked()

        verify(exactly = 1) { listStateReducer.reduce(any(), Action.OverlayClicked) }
    }

    @Test
    fun `WHEN two navigation actions are dispatched close to each other THEN only allow one to execute`() {
        sut.onListItemClicked(mockk(relaxed = true), mockk())
        testScheduler.advanceTimeBy(
            ListViewModel.THROTTLE_WINDOW_NAVIGATION_ACTION_MS - 1,
            TimeUnit.MILLISECONDS
        )
        sut.onSettingsButtonClicked()

        verify(exactly = 1) { navigator.navigateToOpenGS(any(), any(), any()) }
        verify(exactly = 0) { navigator.navigateToSettings() }
    }

    @Test
    fun `GIVEN optionLayout is open WHEN onBackPressed THEN return true`() {
        every { listStateReducer.reduce(any(), any()) } returns State.default()
            .copy(optionsLayoutIsOpen = true)

        sut.onArrowButtonClicked() // This just sets the state
        val backPressedHandledInViewModel = sut.onBackPressed()

        assertEquals(true, backPressedHandledInViewModel)
    }

    @Test
    fun `GIVEN optionLayout is not open WHEN onBackPressed THEN return true`() {
        every { listStateReducer.reduce(any(), any()) } returns State.default()
            .copy(optionsLayoutIsOpen = false)

        sut.onArrowButtonClicked() // This just sets the state
        val backPressedHandledInViewModel = sut.onBackPressed()

        assertEquals(false, backPressedHandledInViewModel)
    }

    @Test
    fun `WHEN viewmodel is cleared THEN repository is cleared`() {
        sut.onCleared()
        verify(exactly = 1) { repository.clear() }
    }
}
