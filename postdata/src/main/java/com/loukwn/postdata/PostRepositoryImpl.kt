package com.loukwn.postdata

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
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named

internal class PostRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val postApi: PostApi,
    private val sharedPrefsHelper: SharedPrefsHelper,
    @Named("io") private val ioScheduler: Scheduler
) : PostRepository {

    private var postFetchDisposable: Disposable? = null
    private var authTokenDisposable: Disposable? = null

    override val postDataObservable = PublishSubject.create<DataState<PostResponse>>()

    override fun getPosts(sourceType: SourceTypeDTO, filterType: FilterTypeDTO, after: String) {

        postFetchDisposable?.dispose()
        postFetchDisposable = if (authTokenIsValid()) {
            getPostsFromReddit(sourceType, filterType, after)
                .subscribeOn(ioScheduler)
                .subscribeBy(
                    onSuccess = {
                        Timber.i("Posts fetched! After=${it.data.after}")
                    },
                    onError = {
                        Timber.i("Posts not fetched! Error: ${it.message}. Stacktrace: ${it.stackTrace}")
                        postDataObservable.onNext(DataState.Error(it, it.message))
                    }
                )
        } else {
            getNewAuthTokenObservable()
                .subscribeOn(ioScheduler)
                .doOnSuccess { saveTokenToPrefs(it) }
                .flatMap {
                    getPostsFromReddit(sourceType, filterType, after)
                        .subscribeOn(ioScheduler)
                }
                .subscribeBy(
                    onSuccess = {
                        Timber.i("Posts fetched! After=${it.data.after}")
                    },
                    onError = {
                        Timber.i("Posts not fetched! Error: ${it.message}. Stacktrace: ${it.stackTrace}")
                        postDataObservable.onNext(DataState.Error(it, it.message))
                    }
                )
        }
    }

    override fun refreshAuthTokenIfNeeded() {
        if (!authTokenIsValid()) {
            authTokenDisposable?.dispose()
            authTokenDisposable = getNewAuthTokenObservable().subscribeOn(ioScheduler).subscribe()
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
        sourceType: SourceTypeDTO,
        filterType: FilterTypeDTO,
        after: String
    ): Single<RedditSubredditResponse> {

        val token = sharedPrefsHelper[SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, ""]

        val request = when (filterType) {
            FilterTypeDTO.Hot -> postApi.getHotGifSounds(
                subName = sourceType.apiLabel,
                tokenData = "bearer $token",
                userAgent = BuildConfig.RedditUserAgent,
                after = after,
            )
            FilterTypeDTO.New -> postApi.getNewGifSounds(
                subName = sourceType.apiLabel,
                tokenData = "bearer $token",
                userAgent = BuildConfig.RedditUserAgent,
                after = after,
            )
            is FilterTypeDTO.Top -> postApi.getTopGifSounds(
                subName = sourceType.apiLabel,
                tokenData = "bearer $token",
                userAgent = BuildConfig.RedditUserAgent,
                after = after,
                time = filterType.type.apiLabel,
            )
        }

        return request
            .subscribeOn(ioScheduler)
            .doOnSuccess { postDataObservable.onNext(DataState.Data(it.toDomainData())) }
    }

    private fun getNewAuthTokenObservable(): Single<RedditTokenResponse> =
        authApi.getAuthToken(
            REDDIT_GRANT_TYPE,
            UUID.randomUUID().toString()
        )

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

