package com.loukwn.postdata

import android.app.Application
import androidx.annotation.StringRes
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
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named

// TODO make Singleton?
class PostRepository @Inject constructor(
    private val context: Application,
    private val authApi: AuthApi,
    private val postApi: PostApi,
    private val sharedPrefsHelper: SharedPrefsHelper,
    @Named("io") private val ioScheduler: Scheduler
) {
    private val postErrorMessage by lazy {
        context.resources.getString(R.string.list_error_posts)
    }

    private var postFetchDisposable: Disposable? = null
    private var authTokenDisposable: Disposable? = null

    val postDataObservable: PublishSubject<DataState<PostResponse>> = PublishSubject.create()

    fun getPosts(filterType: FilterType, after: String) {

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

    fun refreshAuthTokenIfNeeded() {
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
        filterType: FilterType,
        after: String
    ): Single<RedditSubredditResponse> {

        val token = sharedPrefsHelper[SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, ""]

        val request = when (filterType) {
            FilterType.Hot -> postApi.getHotGifSounds(
                "bearer $token",
                BuildConfig.RedditUserAgent,
                after
            )
            FilterType.New -> postApi.getNewGifSounds(
                "bearer $token",
                BuildConfig.RedditUserAgent,
                after
            )
            is FilterType.Top -> postApi.getTopGifSounds(
                "bearer $token",
                BuildConfig.RedditUserAgent,
                after,
                filterType.type.apiLabel
            )
        }

        return request
            .subscribeOn(ioScheduler)
            .doOnSuccess { postDataObservable.onNext(DataState.Data(it.toDomainData())) }
    }

    private fun getNewAuthTokenObservable(): Single<RedditTokenResponse> {
        return authApi.getAuthToken(
            RedditConstants.REDDIT_GRANT_TYPE,
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

    fun clear() {
        authTokenDisposable?.dispose()
        authTokenDisposable = null
        postFetchDisposable?.dispose()
        postFetchDisposable = null
    }
}

object RedditConstants {
    const val REDDIT_AUTH_BASE_URL = "https://www.reddit.com/"
    const val REDDIT_POST_BASE_URL = "https://oauth.reddit.com/"
    const val REDDIT_GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client"
    const val NUM_OF_POSTS_PER_REQUEST = 25
}

sealed class FilterType {
    object Hot : FilterType()
    object New : FilterType()
    data class Top(val type: TopFilterType) : FilterType()
}

enum class TopFilterType(val apiLabel: String, @StringRes val uiLabelRes: Int) {
    HOUR(apiLabel = "hour", uiLabelRes = R.string.list_top_hour),
    DAY(apiLabel = "day", uiLabelRes = R.string.list_top_day),
    WEEK("week", uiLabelRes = R.string.list_top_week),
    MONTH("month", uiLabelRes = R.string.list_top_month),
    YEAR("year", uiLabelRes = R.string.list_top_year),
    ALL("all", uiLabelRes = R.string.list_top_all),
}


