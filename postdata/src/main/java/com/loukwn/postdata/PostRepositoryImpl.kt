package com.loukwn.postdata

import android.app.Application
import com.kostaslou.gifsoundit.common.disk.SharedPrefsHelper
import com.kostaslou.gifsoundit.common.util.DataState
import com.loukwn.postdata.model.api.RedditSubredditResponse
import com.loukwn.postdata.model.api.RedditTokenResponse
import com.loukwn.postdata.model.api.toDomainData
import com.loukwn.postdata.model.domain.PostResponse
import com.loukwn.postdata.network.AuthApi
import com.loukwn.postdata.network.PostApi
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import java.util.*
import javax.inject.Inject
import javax.inject.Named

internal class PostRepositoryImpl @Inject constructor(
    private val context: Application,
    private val authApi: AuthApi,
    private val postApi: PostApi,
    private val sharedPrefsHelper: SharedPrefsHelper,
    @Named("io") private val ioScheduler: Scheduler
): PostRepository {
    private val postErrorMessage by lazy {
        context.resources.getString(R.string.list_error_posts)
    }

    private var postFetchDisposable: Disposable? = null
    private var authTokenDisposable: Disposable? = null

    override val postDataObservable = PublishSubject.create<DataState<PostResponse>>()

    override fun getPosts(sourceType: SourceTypeDTO, filterType: FilterTypeDTO, after: String) {

        postFetchDisposable?.dispose()
        postFetchDisposable = if (authTokenIsValid()) {
            getPostsFromReddit(filterType, after)
                .subscribeBy(onError = {
                    postDataObservable.onNext(DataState.Error(it, postErrorMessage))
                })
        } else {
            getNewAuthTokenObservable()
                .flatMap { getPostsFromReddit(filterType, after) }
                .subscribeBy(onError = {
                    postDataObservable.onNext(DataState.Error(it, postErrorMessage))
                })
        }
    }

    override fun refreshAuthTokenIfNeeded() {
        if (!authTokenIsValid()) {
            authTokenDisposable = getNewAuthTokenObservable().subscribe()
        }
    }

    private fun authTokenIsValid(): Boolean {
        val now = Date()

        val accessToken: String =
            sharedPrefsHelper[SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, ""] ?: ""
        val expiresAtDate =
            Date(sharedPrefsHelper[SharedPrefsHelper.PREF_KEY_EXPIRES_AT, now.time])

        return !(expiresAtDate.before(now) || expiresAtDate == now || accessToken.isEmpty())
    }

    private fun getPostsFromReddit(
        filterType: FilterTypeDTO,
        after: String
    ): Single<RedditSubredditResponse> {

        val token = sharedPrefsHelper[SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, ""]
//
//        val request = when (filterType) {
//            FilterTypeDTO.Hot -> postApi.getHotGifSounds(
//                "bearer $token",
//                BuildConfig.RedditUserAgent,
//                after
//            )
//            FilterTypeDTO.New -> postApi.getNewGifSounds(
//                "bearer $token",
//                BuildConfig.RedditUserAgent,
//                after
//            )
//            is FilterTypeDTO.Top -> postApi.getTopGifSounds(
//                "bearer $token",
//                BuildConfig.RedditUserAgent,
//                after,
//                filterType.type.apiLabel
//            )
//        }
        val request = postApi.getHotGifSounds(
            "bearer $token",
            BuildConfig.RedditUserAgent,
            after
        )

        return request
            .subscribeOn(ioScheduler)
            .doOnSuccess { postDataObservable.onNext(DataState.Data(it.toDomainData())) }
    }

    private fun getNewAuthTokenObservable(): Single<RedditTokenResponse> {
        return authApi.getAuthToken(
            REDDIT_GRANT_TYPE,
            UUID.randomUUID().toString()
        )
            .subscribeOn(ioScheduler)
            .doOnSuccess { saveTokenToPrefs(it) }
    }

    private fun saveTokenToPrefs(r: RedditTokenResponse) {

        val date = Date(Date().time + r.expires_in.toLong() * 1000)

        sharedPrefsHelper.put(SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, r.access_token)
        sharedPrefsHelper.put(SharedPrefsHelper.PREF_KEY_EXPIRES_AT, date.time)
    }

    override fun clear() {
        authTokenDisposable?.dispose()
        authTokenDisposable = null
        postFetchDisposable?.dispose()
        postFetchDisposable = null
    }

    companion object {
        private const val REDDIT_GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client"
    }
}

