package com.kostaslou.gifsoundit.ui.home

import androidx.lifecycle.ViewModel
import com.kostaslou.gifsoundit.util.commons.PostType
import com.kostaslou.gifsoundit.data.Repository
import com.kostaslou.gifsoundit.data.disk.SharedPrefsHelper
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


class HomeViewModel @Inject constructor(private val repository: Repository,
                                        private val sharedPrefsHelper: SharedPrefsHelper) : ViewModel() {


    private val compositeDisposable = CompositeDisposable()

    // state
    var postType = PostType.HOT
    var topType = "all"
    var filterMenuVisible = false

    // get data from repository

    fun getPosts() {

    }

    fun getNextPage() {

    }

//    val postsOutcome: LiveData<Outcome<List<Post>>> by lazy {
//        //Convert publishSubject to livedata
//        repository.postFetchOutcome.toLiveData(compositeDisposable)
//    }

    // button interactions

    fun moreClicked() {
        filterMenuVisible = !filterMenuVisible
    }

    fun categoryChanged(newPostType: PostType, newTopType: String = "all") {
        if (newPostType==postType || (newPostType==PostType.TOP && newTopType == topType)) return

        postType = newPostType
        topType = newTopType

        getPosts()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
     }
}