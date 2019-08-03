package com.kostaslou.gifsoundit.ui.home

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kostaslou.gifsoundit.data.Repository
import com.kostaslou.gifsoundit.data.api.model.RedditPostResponse
import com.kostaslou.gifsoundit.ui.home.model.PostModel
import com.kostaslou.gifsoundit.util.commons.Message
import com.kostaslou.gifsoundit.util.commons.MessageCodes
import com.kostaslou.gifsoundit.util.commons.PostType
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    // for the cleanup
    private val compositeDisposable = CompositeDisposable()

    // state
    private var postType = PostType.HOT
    private var topType = "all"
    private var after = ""
    private var before = ""
    private var filterMenuVisible = false

    // livedata objects
    val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val postsLiveData: MutableLiveData<List<PostModel>> = MutableLiveData()
    var messageLiveData: MutableLiveData<Message> = MutableLiveData()

    init {
        listenToRepoLiveData()
    }

    fun getPostType() = postType
    fun getFilterMenuVisible() = filterMenuVisible

    @SuppressLint("CheckResult")
    private fun listenToRepoLiveData() {

        // repo has refreshed token
        repository.tokenIsReadyObservable.subscribe {
            if (it) messageLiveData.value = Message.info(MessageCodes.TOKEN_READY)
        }

        // repo has post data for us
        repository.postDataObservable.subscribe {

            var clearData = false
            if (after == "") clearData = true

            after = it.data.after ?: ""
            before = it.data.before ?: ""

            val receivedData = transformResponseToAdapterType(it)

            postsLiveData.value = if (clearData)
                receivedData
            else
                postsLiveData.value?.plus(receivedData) ?: receivedData

            loadingLiveData.value = false
        }

        // repo has error
        repository.postErrorObservable.subscribe {
            messageLiveData.value = Message.error(it)
            loadingLiveData.value = false
        }
    }

    // communicate with repository
    fun getPosts(firstTime: Boolean = false, refresh: Boolean = false, showLoading: Boolean = true) {

        if (postsLiveData.value == null || !firstTime) {
            if (postsLiveData.value != null && showLoading) loadingLiveData.value = true
            if (refresh) after = ""

            repository.getPosts(postType, after, topType)
        } else {
            // first post requesting after config change
            messageLiveData.value = Message.info(MessageCodes.RECREATED)
        }
    }

    // transform response to a type that the view understands
    private fun transformResponseToAdapterType(r: RedditPostResponse): List<PostModel> {
        return r.data.children.map {
            val item = it.data

            val url = if (item.url.startsWith("/r")) "http://www.reddit.com" + item.url else item.url
            val perma = if (item.permalink.startsWith("/r")) "http://www.reddit.com" + item.permalink else item.permalink
            val created = item.created_utc
            val score = item.score
            val isSelf = item.is_self

            PostModel(item.title, item.thumbnail, created, score, url, perma, isSelf)
        }
    }

    // button interactions

    fun moreClicked() {
        filterMenuVisible = !filterMenuVisible
    }

    fun categoryChanged(newPostType: PostType, newTopType: String = "all") {
        if (newPostType == postType && newPostType != PostType.TOP) return

        postType = newPostType
        topType = newTopType
        after = ""

        getPosts()
    }

    // lifecycle-based

    fun resetMessage() {
        messageLiveData = MutableLiveData()
    }

    fun homeResumed() {
        repository.refreshAuthToken()
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("cleared")

        compositeDisposable.clear()
        repository.clearDisposables()
    }
}