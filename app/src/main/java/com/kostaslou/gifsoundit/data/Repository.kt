package com.kostaslou.gifsoundit.data

import android.text.TextUtils
import com.kostaslou.gifsoundit.BuildConfig
import com.kostaslou.gifsoundit.data.api.AuthApi
import com.kostaslou.gifsoundit.data.api.PostApi
import com.kostaslou.gifsoundit.data.api.model.RedditPostResponse
import com.kostaslou.gifsoundit.data.api.model.RedditTokenResponse
import com.kostaslou.gifsoundit.data.disk.SharedPrefsHelper
import com.kostaslou.gifsoundit.util.RxSchedulers
import com.kostaslou.gifsoundit.util.commons.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.subjects.PublishSubject
import java.util.*
import javax.inject.Inject


// the main api with the initializer
class Repository @Inject constructor(private val authApi: AuthApi,
                                      val postApi: PostApi,
                                     private val sharedPrefsHelper: SharedPrefsHelper,
                                     private val rxSchedulers: RxSchedulers) {

    private val compositeDisposable = CompositeDisposable()

    // observables for the ViewModel
    val postDataObservable: PublishSubject<RedditPostResponse> = PublishSubject.create()
    val postErrorObservable: PublishSubject<Throwable> = PublishSubject.create()
    val tokenIsReadyObservable: PublishSubject<Boolean> = PublishSubject.create()


    // if the token is ok, fetches the data from the api
    fun getPosts(postType: PostType, after: String, topType: String = "all") {

        getAuthToken()?.let {

            val disposable = when (postType) {
                PostType.HOT -> postApi.getHotGifSounds("bearer $it", BuildConfig.RedditUserAgent, after)
                PostType.NEW -> postApi.getNewGifSounds("bearer $it", BuildConfig.RedditUserAgent, after)
                PostType.TOP -> postApi.getTopGifSounds("bearer $it", BuildConfig.RedditUserAgent, after, topType)
            }

            compositeDisposable.add(disposable
                    .schedulerSetup(rxSchedulers)
                    .subscribeWith(object : DisposableSingleObserver<RedditPostResponse>() {
                        override fun onSuccess(t: RedditPostResponse) {
                            postDataObservable.onNext(t)
                        }

                        override fun onError(e: Throwable) {
                            postErrorObservable.onNext(PostsHttpException(e))
                        }
                    }))
        }
    }

    // returns true if token needed to be refreshed, false otherwise (and do it meanwhile)
    fun refreshAuthToken(): Boolean {
        return getAuthToken() != null
    }

    // updates token in needed
    private fun getAuthToken(): String? {

        val today = Date()

        val accessToken : String = sharedPrefsHelper[SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, ""] ?: ""
        val expiresAtDate = Date(sharedPrefsHelper[SharedPrefsHelper.PREF_KEY_EXPIRES_AT, today.time] ?: today.time)

        if (expiresAtDate.before(today) || expiresAtDate == today || TextUtils.isEmpty(accessToken)) {
            // we need to update the access token

            compositeDisposable.add(authApi.getAuthToken(RedditConstants.REDDIT_GRANT_TYPE, UUID.randomUUID().toString())
                    .schedulerSetup(rxSchedulers)
                    .subscribeWith(object : DisposableSingleObserver<RedditTokenResponse>() {
                        override fun onSuccess(t: RedditTokenResponse) {
                            saveTokenToPrefs(t)
                            tokenIsReadyObservable.onNext(true)
                        }

                        override fun onError(e: Throwable) {
                            postErrorObservable.onNext(TokenHttpException(e))
                        }
                    }))

            return null
        }

        return accessToken
    }

    // saves token to prefs
    private fun saveTokenToPrefs(r: RedditTokenResponse) {

        val date = Date(Date().time + r.expires_in.toLong() * 1000)

        sharedPrefsHelper.put(SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, r.access_token)
        sharedPrefsHelper.put(SharedPrefsHelper.PREF_KEY_EXPIRES_AT, date.time)
    }

    // cleanup
    fun clearDisposables() {
        compositeDisposable.clear()
    }
}