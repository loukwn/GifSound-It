//package com.kostaslou.gifsoundit
//
//import android.text.TextUtils
//import com.kostaslou.gifsoundit.data.Repository
//import com.kostaslou.gifsoundit.data.disk.SharedPrefsHelper
//import com.kostaslou.gifsoundit.util.RxSchedulers
//import com.kostaslou.gifsoundit.util.commons.LocalPostData
//import com.kostaslou.gifsoundit.util.commons.PostModel
//import com.kostaslou.gifsoundit.util.commons.RedditPostResponse
//import com.kostaslou.gifsoundit.util.commons.RedditTokenResponse
//import io.reactivex.disposables.CompositeDisposable
//import io.reactivex.disposables.Disposable
//import io.reactivex.observers.DisposableSingleObserver
//import io.reactivex.subjects.PublishSubject
//import java.util.*
//
//class HomeViewModel(mRepository: Repository, sharedPreferences: SharedPrefsHelper, schedulers: RxSchedulers) {
//
//    private var repository: Repository = mRepository
//    private var sharedPrefsHelper: SharedPrefsHelper = sharedPreferences
//    private var rxSchedulers: RxSchedulers = schedulers
//
//    // observables for the view
//    var resultPostsObservable: PublishSubject<LocalPostData> = PublishSubject.create()
//    var resultTokenObservable: PublishSubject<String> = PublishSubject.create()
//    var resultErrorObservable: PublishSubject<String> = PublishSubject.create()
//
//    // for the cleanup
//    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
//
//    fun getPosts(after: String, postType: Int, topType: String = "all") {
//
//        // access token from shared prefs
//        val accessToken : String = sharedPrefsHelper[SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, ""] ?: ""
//        val expiresAtDate = Date(sharedPrefsHelper[SharedPrefsHelper.PREF_KEY_EXPIRES_AT, Date().time] ?: Date().time)
//
//        if (expiresAtDate.before(Date()) || expiresAtDate == Date() || TextUtils.isEmpty(accessToken)) {
//            // we need to update the access token
//
////            resultErrorObservable.onNext(provideMessageFromException(TokenRequiredException("Token required")))
//            getAccessToken()
//            return
//        }
//
//        // communicate with repo
////        val disposable: Disposable = repository.getPostsFromNetwork(accessToken, postType, after, topType)
////            .subscribeOn(rxSchedulers.ioScheduler)
////            .observeOn(rxSchedulers.androidScheduler)
////            .subscribeWith(object : DisposableSingleObserver<RedditPostResponse>() {
////                override fun onSuccess(t: RedditPostResponse) {
////                    resultPostsObservable.onNext(transformResponseToAdapterType(t))
////                }
////
////                override fun onError(e: Throwable) {
////                    Log.v("onError", e.message ?: "unknown error i guess?")
////                    resultErrorObservable.onNext("error getting posts")
////                }
////            })
////        compositeDisposable.add(disposable)
//    }
//
//    // access token
//    private fun getAccessToken() {
//
//        // communicate with repo
//        val disposable: Disposable = repository.getRedditAuthToken()
//            .subscribeOn(rxSchedulers.ioScheduler)
//            .observeOn(rxSchedulers.androidScheduler)
//            .subscribeWith(object : DisposableSingleObserver<RedditTokenResponse>() {
//                override fun onSuccess(t: RedditTokenResponse) {
//                    resultTokenObservable.onNext(saveTokenToPrefsAndReturnName(t))
//                }
//
//                override fun onError(e: Throwable) {
//                    resultErrorObservable.onNext("error getting token")
//                }
//            })
//        compositeDisposable.add(disposable)
//    }
//
//    // save token to prefs
//    private fun saveTokenToPrefsAndReturnName(r: RedditTokenResponse) : String {
//
//        // save token to prefs
//        val date = Date(Date().time + r.expires_in.toLong() * 1000)
//
//        sharedPrefsHelper.put(SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, r.access_token)
//        sharedPrefsHelper.put(SharedPrefsHelper.PREF_KEY_EXPIRES_AT, date.time)
//
//        return r.access_token
//    }
//
//    // transform response to a type that the view understands
//    private fun transformResponseToAdapterType(r: RedditPostResponse) : LocalPostData {
//        val posts = r.data.children.map {
//            val item = it.data
//
//            val url = if (item.url.startsWith("/r")) "http://www.reddit.com"+item.url else item.url
//            val perma = if (item.permalink.startsWith("/r")) "http://www.reddit.com"+item.permalink else item.permalink
//            val created = item.created_utc
//            val score = item.score
//            val isSelf = item.is_self
//
//            PostModel(item.title, item.thumbnail, created, score, url, perma, isSelf)
//        }
//
//        return LocalPostData(posts, r.data.before ?: "", r.data.after ?: "")
//    }
//
////    // exception from retrofit -> error message
////    private fun provideMessageFromException(e: Exception): String {
////        // todo: translate the strings for the view
////
////        return when (e) {
////            is PostsHttpException -> "Could not fetch posts.. Code: " + e.httpException.code() + " Message: " + e.httpException.message()
////            is TokenHttpException -> "Could not get token.. Code: " + e.httpException.code() + " Message: " + e.httpException.message()
////            is TokenRequiredException -> "Refreshing access token.."
////            else -> "Error getting data..."
////        }
////    }
//
//    fun cancelNetworkConnections() {
//        compositeDisposable.clear()
//    }
//}