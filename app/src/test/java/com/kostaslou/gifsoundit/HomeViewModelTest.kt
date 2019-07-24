package com.kostaslou.gifsoundit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kostaslou.gifsoundit.data.Repository
import com.kostaslou.gifsoundit.data.api.model.RedditDataResponse
import com.kostaslou.gifsoundit.data.api.model.RedditNewsDataResponse
import com.kostaslou.gifsoundit.data.api.model.RedditPostChildrenResponse
import com.kostaslou.gifsoundit.data.api.model.RedditPostResponse
import com.kostaslou.gifsoundit.ui.home.HomeViewModel
import com.kostaslou.gifsoundit.util.commons.Message
import com.kostaslou.gifsoundit.util.commons.MessageCodes
import com.kostaslou.gifsoundit.util.commons.PostType
import com.nhaarman.mockitokotlin2.*
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito
import kotlin.test.assertEquals


class HomeViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: HomeViewModel
    private lateinit var mockedRepo: Repository


    @Before
    fun setUp() {
        // mock repo and init ViewModel

        mockedRepo = mock {
            on { postDataObservable } doReturn PublishSubject.create()
            on { tokenIsReadyObservable } doReturn PublishSubject.create()
            on { postErrorObservable } doReturn PublishSubject.create()
        }
        viewModel = HomeViewModel(mockedRepo)
    }

    @Test
    fun `when repo returns post data`() {

        // given that repo notifies the viewmodel that some data are available...
        val data = RedditPostChildrenResponse(RedditNewsDataResponse("", "", 0, false, 0, "", ""))
        mockedRepo.postDataObservable.onNext(RedditPostResponse(RedditDataResponse(listOf(data), "", "")))

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
        spy.categoryChanged(PostType.HOT)
        verify(spy, never()).getPosts()
    }

    @Test
    fun `when view changes to different category`() {

        // we are on hot and change to top.. Fetch normally
        val spy = Mockito.spy(viewModel)
        spy.categoryChanged(PostType.TOP)
        verify(spy).getPosts()
    }

    @Test
    fun `when view changes to same category but is top`() {

        // we are on top and change to top.. Fetch normally
        viewModel.categoryChanged(PostType.TOP)
        val spy = Mockito.spy(viewModel)
        spy.categoryChanged(PostType.TOP)
        verify(spy).getPosts()
    }
}
