package com.kostaslou.gifsoundit

import com.kostaslou.gifsoundit.data.Repository
import com.kostaslou.gifsoundit.data.api.AuthApi
import com.kostaslou.gifsoundit.data.api.PostApi
import com.kostaslou.gifsoundit.data.api.model.RedditPostResponse
import com.kostaslou.gifsoundit.data.api.model.RedditTokenResponse
import com.kostaslou.gifsoundit.data.disk.SharedPrefsHelper
import com.kostaslou.gifsoundit.util.RxSchedulers
import com.kostaslou.gifsoundit.util.commons.PostType
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import io.reactivex.observers.TestObserver
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
    private lateinit var repo: Repository

    @Mock
    private lateinit var authApi: AuthApi

    @Mock
    private lateinit var postApi: PostApi

    @Mock
    private lateinit var sharedPrefsHelper: SharedPrefsHelper

    private val rxSchedulers = RxSchedulers.test()

    // observable observers
    private val dataObserver = TestObserver<RedditPostResponse>()
    private val errorObserver = TestObserver<Throwable>()
    private val tokenIsReadyObserver = TestObserver<Boolean>()

    // others
    private val today = Date()

    @Before
    fun setup() {
        // init

        repo = Repository(authApi, postApi, sharedPrefsHelper, rxSchedulers)
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
        repo.getPosts(PostType.HOT, "", "all")

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
        repo.getPosts(PostType.HOT, "", "all")

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
        repo.getPosts(PostType.HOT, "", "all")

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
        repo.getPosts(PostType.HOT, "", "all")

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
        whenever(postApi.getHotGifSounds(any(), any(), any())).thenReturn(Single.just(RedditPostResponse(mock())))
        whenever(postApi.getNewGifSounds(any(), any(), any())).thenReturn(Single.just(RedditPostResponse(mock())))
        whenever(postApi.getTopGifSounds(any(), any(), any(), any())).thenReturn(Single.just(RedditPostResponse(mock())))
        // ...assert that when...

        // 1) hot posts are requested, hot posts are queried from the api
        repo.getPosts(PostType.HOT, "", "all")
        verify(postApi).getHotGifSounds(any(), any(), any())

        // 2) new posts are requested, new posts are queried from the api
        repo.getPosts(PostType.NEW, "", "all")
        verify(postApi).getNewGifSounds(any(), any(), any())

        // 3) top posts are requested, top posts are queried from the api
        repo.getPosts(PostType.TOP, "", "all")
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
        repo.getPosts(PostType.HOT, "", "all")
        verify(postApi).getHotGifSounds(any(), any(), any())

        // 2) new posts are requested, new posts are queried from the api
        repo.getPosts(PostType.NEW, "", "all")
        verify(postApi).getNewGifSounds(any(), any(), any())

        // 3) top posts are requested, top posts are queried from the api
        repo.getPosts(PostType.TOP, "", "all")
        verify(postApi).getTopGifSounds(any(), any(), any(), any())


        // assert the data in observables
        tokenIsReadyObserver.assertNoValues()
        dataObserver.assertNoValues()
        errorObserver.assertValueCount(3)
    }
}