package com.kostaslou.gifsoundit.ui.home

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kostaslou.gifsoundit.data.Repository
import com.kostaslou.gifsoundit.data.disk.SharedPrefsHelper
import com.kostaslou.gifsoundit.util.RxSchedulers
import com.kostaslou.gifsoundit.util.commons.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import java.util.*
import javax.inject.Inject


class HomeViewModel @Inject constructor(private val repository: Repository,
                                        private val sharedPrefsHelper: SharedPrefsHelper,
                                        private val rxSchedulers: RxSchedulers) : ViewModel() {


    private val compositeDisposable = CompositeDisposable()


    // state
    var postType = PostType.HOT
    var topType = "all"
    var after = ""
    var before = ""
    var filterMenuVisible = false

    // livedata objects
    val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val postsLiveData: MutableLiveData<List<PostModel>> = MutableLiveData()
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()


    // communicate with repository
    fun getPosts(firstTime: Boolean = false, refresh: Boolean = false, showLoading: Boolean = true) {

        if (postsLiveData.value==null || !firstTime) {
            if (postsLiveData.value!=null && showLoading) loadingLiveData.value = true

            if (refresh) after = ""

            getAuthToken()?.let {
                val disposable: Disposable = repository.getPostsFromNetwork(it, postType, after, topType)
                        .schedulerSetup(rxSchedulers)
                        .subscribeWith(object : DisposableSingleObserver<RedditPostResponse>() {
                            override fun onSuccess(t: RedditPostResponse) {

                                var clearData = false
                                if (after == "") clearData = true

                                after = t.data.after ?: ""
                                before = t.data.before ?: ""

                                val receivedData = transformResponseToAdapterType(t)

                                postsLiveData.value = if (clearData)
                                    receivedData
                                else
                                    postsLiveData.value?.plus(receivedData) ?: receivedData

                                loadingLiveData.value = false
                            }

                            override fun onError(e: Throwable) {
                                loadingLiveData.value = false
                                errorLiveData.value = PostsHttpException(e)
                            }
                        })

                compositeDisposable.add(disposable)
            }
        }
    }

    private fun getAuthToken() : String? {
        val today = Date()

        val accessToken : String = sharedPrefsHelper[SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, ""] ?: ""
        val expiresAtDate = Date(sharedPrefsHelper[SharedPrefsHelper.PREF_KEY_EXPIRES_AT, today.time] ?: today.time)

        if (expiresAtDate.before(today) || expiresAtDate == today || TextUtils.isEmpty(accessToken)) {
            // we need to update the access token
            loadingLiveData.value = true

            compositeDisposable.add(repository.getRedditAuthToken()
                    .schedulerSetup(rxSchedulers)
                    .subscribeWith(object : DisposableSingleObserver<RedditTokenResponse>() {
                        override fun onSuccess(t: RedditTokenResponse) {
                            repository.getPostsFromNetwork(saveTokenToPrefsAndReturnIt(t), postType, after, topType)
                        }

                        override fun onError(e: Throwable) {
                            loadingLiveData.value = false
                            errorLiveData.value = TokenHttpException(e)
                        }
                    }))
            return null
        }

        return accessToken
    }

    // transform response to a type that the view understands
    private fun transformResponseToAdapterType(r: RedditPostResponse) : List<PostModel> {
        return r.data.children.map {
            val item = it.data

            val url = if (item.url.startsWith("/r")) "http://www.reddit.com"+item.url else item.url
            val perma = if (item.permalink.startsWith("/r")) "http://www.reddit.com"+item.permalink else item.permalink
            val created = item.created_utc
            val score = item.score
            val isSelf = item.is_self

            PostModel(item.title, item.thumbnail, created, score, url, perma, isSelf)
        }
    }


    // save token to prefs
    private fun saveTokenToPrefsAndReturnIt(r: RedditTokenResponse) : String {

        // save token to prefs
        val date = Date(Date().time + r.expires_in.toLong() * 1000)

        sharedPrefsHelper.put(SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, r.access_token)
        sharedPrefsHelper.put(SharedPrefsHelper.PREF_KEY_EXPIRES_AT, date.time)

        return r.access_token
    }

    // button interactions

    fun moreClicked() {
        filterMenuVisible = !filterMenuVisible
    }

    fun categoryChanged(newPostType: PostType, newTopType: String = "all") {
        if (newPostType==postType && newPostType!=PostType.TOP) return

        postType = newPostType
        topType = newTopType
        after = ""

        getPosts()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
     }
}