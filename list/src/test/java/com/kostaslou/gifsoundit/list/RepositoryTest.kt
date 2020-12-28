package com.kostaslou.gifsoundit.list

import com.kostaslou.gifsoundit.common.disk.SharedPrefsHelper
import com.loukwn.postdata.PostRepository
import com.loukwn.postdata.FilterType
import com.loukwn.postdata.network.AuthApi
import com.loukwn.postdata.network.PostApi
import com.loukwn.postdata.model.api.RedditSubredditResponse
import com.loukwn.postdata.model.api.RedditTokenResponse
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class RepositoryTest {

    // repo and its mock dependencies
    private lateinit var repo: PostRepository

    @Mock
    private lateinit var authApi: AuthApi

    @Mock
    private lateinit var postApi: PostApi

    @Mock
    private lateinit var sharedPrefsHelper: SharedPrefsHelper

    private val ioScheduler = Schedulers.trampoline()
    private val mainScheduler = Schedulers.trampoline()

    // observable observers
    private val dataObserver = TestObserver<RedditSubredditResponse>()
    private val errorObserver = TestObserver<Throwable>()
    private val tokenIsReadyObserver = TestObserver<Boolean>()

    // others
    private val today = Date()

    @Before
    fun setup() {
        // init

        repo = PostRepository(authApi, postApi, sharedPrefsHelper, ioScheduler, mainScheduler)
        repo.postDataObservable.subscribe(dataObserver)
        repo.tokenIsReadyObservable.subscribe(tokenIsReadyObserver)
        repo.postErrorObservable.subscribe(errorObserver)
    }

    @Test
    fun `when posts are requested but there is no "access_token" saved`() {

        // given that shared preferences have only the "expires_at" key saved...
        whenever(sharedPrefsHelper[any(), anyLong()]).thenReturn(today.time + 1000000)

        // ...and authAPi on request will return the data successfully...
        whenever(authApi.getAuthToken(any(), any())).thenReturn(Single.just(RedditTokenResponse("token", "123")))

        // ...get posts...
        repo.getPosts(FilterType.HOT, "", "all")

        // ...verify that token and expired data has been saved to disk...
        verify(sharedPrefsHelper).put(any(), anyString())
        verify(sharedPrefsHelper).put(any(), anyLong())

        // ...and assert the data in observables
        tokenIsReadyObserver.assertValueCount(1)
        tokenIsReadyObserver.assertValue(true)
        dataObserver.assertNoValues()
        errorObserver.assertNoValues()
    }

    @Test
    fun `when posts are requested but there is no "expires_at" saved`() {

        // given that shared preferences have only the "access_token" key saved...
        whenever(sharedPrefsHelper[any(), anyString()]).thenReturn("a keyy")

        // ...and authAPi on request will return the data successfully...
        whenever(authApi.getAuthToken(any(), any())).thenReturn(Single.just(RedditTokenResponse("token", "123")))

        // ...get posts...
        repo.getPosts(FilterType.HOT, "", "all")

        // ...verify that token and expired data has been saved to disk...
        verify(sharedPrefsHelper).put(any(), anyString())
        verify(sharedPrefsHelper).put(any(), anyLong())

        // ...and assert the data in observables
        tokenIsReadyObserver.assertValueCount(1)
        tokenIsReadyObserver.assertValue(true)
        dataObserver.assertNoValues()
        errorObserver.assertNoValues()
    }

    @Test
    fun `when posts are requested but there is nothing saved`() {

        // given that shared preferences have nothing saved, and authApi returns successfully on request...
        whenever(authApi.getAuthToken(any(), any())).thenReturn(Single.just(RedditTokenResponse("token", "123")))

        // ...get posts...
        repo.getPosts(FilterType.HOT, "", "all")

        // ...verify that token and expired data has been saved to disk...
        verify(sharedPrefsHelper).put(any(), anyString())
        verify(sharedPrefsHelper).put(any(), anyLong())

        // ...and assert the data in observables
        tokenIsReadyObserver.assertValueCount(1)
        tokenIsReadyObserver.assertValue(true)
        dataObserver.assertNoValues()
        errorObserver.assertNoValues()
    }

    @Test
    fun `when posts are requested but there is an error while fetching the token`() {

        // given that shared preferences have nothing saved, and authApi returns successfully on request...
        whenever(authApi.getAuthToken(any(), any())).thenReturn(Single.error(Throwable("")))

        // ...get posts...
        repo.getPosts(FilterType.HOT, "", "all")

        // ...verify that token and expired data have not been saved to disk...
        verify(sharedPrefsHelper, never()).put(any(), anyString())
        verify(sharedPrefsHelper, never()).put(any(), anyLong())

        // ... and assert the data in observables
        tokenIsReadyObserver.assertNoValues()
        dataObserver.assertNoValues()
        errorObserver.assertValueCount(1)
    }

    @Test
    fun `when posts are requested from different categories successfully`() {

        // given that shared prefs has everything correctly...
        whenever(sharedPrefsHelper[any(), anyString()]).thenReturn("a key")
        whenever(sharedPrefsHelper[any(), anyLong()]).thenReturn(today.time + 10000000)

        // ...and postApi returns successfully...
        whenever(postApi.getHotGifSounds(any(), any(), any())).thenReturn(Single.just(
            RedditSubredditResponse(mock())
        ))
        whenever(postApi.getNewGifSounds(any(), any(), any())).thenReturn(Single.just(
            RedditSubredditResponse(mock())
        ))
        whenever(postApi.getTopGifSounds(any(), any(), any(), any())).thenReturn(Single.just(
            RedditSubredditResponse(mock())
        ))
        // ...assert that when...

        // 1) hot posts are requested, hot posts are queried from the api
        repo.getPosts(FilterType.HOT, "", "all")
        verify(postApi).getHotGifSounds(any(), any(), any())

        // 2) new posts are requested, new posts are queried from the api
        repo.getPosts(FilterType.NEW, "", "all")
        verify(postApi).getNewGifSounds(any(), any(), any())

        // 3) top posts are requested, top posts are queried from the api
        repo.getPosts(FilterType.TOP, "", "all")
        verify(postApi).getTopGifSounds(any(), any(), any(), any())

        // assert the data in observables
        tokenIsReadyObserver.assertNoValues()
        dataObserver.assertValueCount(3)
        errorObserver.assertNoValues()
    }

    @Test
    fun `when posts are requested but there is an error during the post fetch`() {

        // given that shared prefs has everything correctly...
        whenever(sharedPrefsHelper[SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, ""]).thenReturn("a key")
        whenever(sharedPrefsHelper[any(), anyLong()]).thenReturn(today.time + 10000000)

        // ...and postApi returns an error every time...
        whenever(postApi.getHotGifSounds(any(), any(), any())).thenReturn(Single.error(Throwable("")))
        whenever(postApi.getNewGifSounds(any(), any(), any())).thenReturn(Single.error(Throwable("")))
        whenever(postApi.getTopGifSounds(any(), any(), any(), any())).thenReturn(Single.error(Throwable("")))
        // ...assert that when...

        // 1) hot posts are requested, hot posts are queried from the api
        repo.getPosts(FilterType.HOT, "", "all")
        verify(postApi).getHotGifSounds(any(), any(), any())

        // 2) new posts are requested, new posts are queried from the api
        repo.getPosts(FilterType.NEW, "", "all")
        verify(postApi).getNewGifSounds(any(), any(), any())

        // 3) top posts are requested, top posts are queried from the api
        repo.getPosts(FilterType.TOP, "", "all")
        verify(postApi).getTopGifSounds(any(), any(), any(), any())

        // assert the data in observables
        tokenIsReadyObserver.assertNoValues()
        dataObserver.assertNoValues()
        errorObserver.assertValueCount(3)
    }
}
