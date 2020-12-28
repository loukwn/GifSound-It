package com.kostaslou.gifsoundit.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kostaslou.gifsoundit.list.viewmodel.ListViewModel
import com.kostaslou.gifsoundit.list.util.commons.Message
import com.kostaslou.gifsoundit.list.util.commons.MessageCodes
import com.loukwn.postdata.PostRepository
import com.loukwn.postdata.FilterType
import com.loukwn.postdata.model.api.RedditSubredditDataResponse
import com.loukwn.postdata.model.api.RedditPostChildData
import com.loukwn.postdata.model.api.RedditPostChildren
import com.loukwn.postdata.model.api.RedditSubredditResponse
import com.nhaarman.mockitokotlin2.*
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class ListViewModelTest {

    // instant execution of livedata operations
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ListViewModel
    private lateinit var mockedRepo: PostRepository

    @Before
    fun setUp() {
        // mock repo and init ViewModel

        mockedRepo = mock {
            on { postDataObservable } doReturn PublishSubject.create()
            on { tokenIsReadyObservable } doReturn PublishSubject.create()
            on { postErrorObservable } doReturn PublishSubject.create()
        }
        viewModel = ListViewModel(mockedRepo)
    }

    @Test
    fun `when repo returns post data`() {

        // given that repo notifies the viewmodel that some data are available...
        val data = RedditPostChildren(RedditPostChildData("", "", 0, false, 0, "", ""))
        mockedRepo.postDataObservable.onNext(RedditSubredditResponse(RedditSubredditDataResponse(listOf(data), "", "")))

        // ... assert that the view will get the same data transformed to List<PostModel>
        assertEquals(1, viewModel.postsLiveData.value?.size)
        assertEquals(null, viewModel.messageLiveData.value)
    }

    @Test
    fun `when repo says it has updates regarding the token`() {

        // given that repo notifies the viewmodel that there are updates for the token,
        // assert that the view will be notified accordingly
        mockedRepo.tokenIsReadyObservable.onNext(false)
        assertEquals(null, viewModel.messageLiveData.value)
        mockedRepo.tokenIsReadyObservable.onNext(true)
        assertEquals(Message.info(MessageCodes.TOKEN_READY), viewModel.messageLiveData.value)
    }

    @Test
    fun `when repo notifies that there are errors`() {

        // given that repo notifies the viewmodel that there are errors,
        // assert that the view is notified
        val t = Throwable()
        mockedRepo.postErrorObservable.onNext(t)
        assertEquals(Message.error(t), viewModel.messageLiveData.value)
    }

    @Test
    fun `when view requests posts for the first time ever`() {

        // first time ever
        viewModel.getPosts()
        assertEquals(null, viewModel.loadingLiveData.value)
        verify(mockedRepo).getPosts(any(), any(), any())
    }

    @Test
    fun `when view wants to refresh not preexisting data`() {

        // same as first ever request (actually not used)
        viewModel.getPosts(refresh = true)
        assertEquals(null, viewModel.loadingLiveData.value)
        verify(mockedRepo).getPosts(any(), any(), any())
    }

    @Test
    fun `when view wants to refresh preexisting data`() {

        // normal refresh
        viewModel.postsLiveData.value = mock()
        viewModel.getPosts(refresh = true)
        assertEquals(true, viewModel.loadingLiveData.value)
        verify(mockedRepo).getPosts(any(), any(), any())
    }

    @Test
    fun `when view changes to same category`() {

        // we are on hot and change to hot.. Posts should not be fetched
        val spy = Mockito.spy(viewModel)
        spy.categoryChanged(FilterType.HOT)
        verify(spy, never()).getPosts()
    }

    @Test
    fun `when view changes to different category`() {

        // we are on hot and change to top.. Fetch normally
        val spy = Mockito.spy(viewModel)
        spy.categoryChanged(FilterType.TOP)
        verify(spy).getPosts()
    }

    @Test
    fun `when view changes to same category but is top`() {

        // we are on top and change to top.. Fetch normally
        viewModel.categoryChanged(FilterType.TOP)
        val spy = Mockito.spy(viewModel)
        spy.categoryChanged(FilterType.TOP)
        verify(spy).getPosts()
    }
}
