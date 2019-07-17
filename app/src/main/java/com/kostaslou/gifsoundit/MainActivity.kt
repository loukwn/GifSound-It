package com.kostaslou.gifsoundit

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.kostaslou.gifsoundit.adapter.MainPostAdapter
import com.kostaslou.gifsoundit.commons.LocalPostData
import com.kostaslou.gifsoundit.commons.PostType
import com.kostaslou.gifsoundit.util.InfiniteScrollListener
import com.mikepenz.aboutlibraries.LibsBuilder
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.view.animation.AnimationUtils
import com.kostaslou.gifsoundit.commons.GeneralConstants
import com.kostaslou.gifsoundit.data.Repository
import com.kostaslou.gifsoundit.data.disk.SharedPrefsHelper
import com.kostaslou.gifsoundit.util.RxSchedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity() {

    private var subscriptions = CompositeDisposable()
    private val mainPresenter by lazy { HomeViewModel(Repository(), SharedPrefsHelper(getSharedPreferences("reddit_stuff", Context.MODE_PRIVATE)), RxSchedulers(Schedulers.io(), AndroidSchedulers.mainThread())) }
    private var localPosts : LocalPostData? = null
    private var accessToken : String? = null
    private var isRefreshing = false
    private var performClear = false
    private var filterMenuVisible = false
    private var postType = 1  // 1 -> HOT, 2 -> TOP, 3 -> NEW
    private var topType = "all"   // hour, day, week etc
    private var stoppedAt : Long? = null

    private var linearLayout: androidx.recyclerview.widget.LinearLayoutManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init
        setContentView(R.layout.activity_main)
        initUI()
        listenToObservables()

        // retain the data during orientation change
        if (savedInstanceState != null && savedInstanceState.containsKey("posts")) {
            localPosts = savedInstanceState.get("posts") as LocalPostData
            (mainRecycler.adapter as MainPostAdapter).clearAndAddPosts(localPosts!!.list)
        }
    }

    override fun onResume() {
        super.onResume()

        // update the posts if 5 minutes, or more have passed
        stoppedAt?.let {
            val start = stoppedAt ?: return
            if ( (System.currentTimeMillis()-start) / (1000.0 * 60.0) > GeneralConstants.MINUTES_TO_REFRESH) {
                // 5 minutes have passed
                requestPosts(true, postType)
            }
        } ?: requestPosts(true, postType)
    }

    override fun onPause() {
        super.onPause()

        mainPresenter.cancelNetworkConnections()
    }

    override fun onStop() {
        mSwipe.isRefreshing = false
        isRefreshing = false
        stoppedAt = System.currentTimeMillis()
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handling of option menu clicks.

        when (item.itemId) {
            R.id.action_about -> {

                // show about section
                LibsBuilder()
                        .withAboutAppName(getString(R.string.app_name))
                        .withAboutIconShown(true)
                        .withAboutVersionShown(true)
                        .withAboutDescription("A mobile version of the website gifsound.com! Posts are fetched from /r/GifSound. By Konstantinos Lountzis.")
                        .start(this)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // we parcel the local data so that we can retain them during orientation change
        super.onSaveInstanceState(outState)
        val posts = (mainRecycler.adapter as MainPostAdapter).getPosts()
        if (localPosts != null && posts.isNotEmpty()) {
            outState.putParcelable("posts", localPosts?.copy(list = posts))
        }
    }

    @SuppressLint("CheckResult")
    private fun listenToObservables() {
        mainPresenter.resultPostsObservable.subscribe {
            // we have data
            if (!performClear) {
                // append data to list
                (mainRecycler.adapter as MainPostAdapter).addPosts(it.list)
                if (TextUtils.isEmpty(localPosts?.after ?: ""))
                    mainRecycler.scrollToPosition(0)
                localPosts = it

            } else {
                // clear and add
                performClear = false

                (mainRecycler.adapter as MainPostAdapter).clearAndAddPosts(it.list)
                mainRecycler.scrollToPosition(0)
                localPosts = it

            }

            mSwipe.isEnabled = true
            mSwipe.isRefreshing = false
            isRefreshing = false

            if (linearLayout!=null) {
                mainRecycler.clearOnScrollListeners()
                mainRecycler.addOnScrollListener(InfiniteScrollListener({ requestPosts(false) }, linearLayout!!))
            }
        }

        mainPresenter.resultTokenObservable.subscribe {
            this.accessToken = it
            val allOk = (!(TextUtils.isEmpty(accessToken ?: "")))

            if (allOk) {
                requestPosts(false, postType)
            }
        }

        mainPresenter.resultErrorObservable.subscribe {
            mSwipe.isRefreshing = false
            isRefreshing = false
            Snackbar.make(mainRecycler, it, Snackbar.LENGTH_LONG).show()
        }
    }

    // Util
    private fun initUI() {

        // toolbar
        setSupportActionBar(toolbar_top)

        // title font
        val typeFace = Typeface.createFromAsset(assets, "fonts/pricedown.ttf")
        toolbarTitle.typeface = typeFace
        toolbarTitle.setOnClickListener {
            if (localPosts!=null)  {
                val lManager = mainRecycler.layoutManager as androidx.recyclerview.widget.LinearLayoutManager
                if (lManager.findFirstVisibleItemPosition() >= GeneralConstants.AMOUNT_OF_VIEWS_TO_INSTA_SCROLL)
                    mainRecycler.scrollToPosition(0)
                else
                    mainRecycler.smoothScrollToPosition(0)
            }
        }

        // slide to refresh
        mSwipe.isEnabled = false
        mSwipe.setOnRefreshListener {
            if (!isRefreshing)
                requestPosts(true, postType)
            else
                mSwipe.isRefreshing = false
        }
        mSwipe.setProgressViewOffset(false, 0, 180)

        // recycler view
        mainRecycler.setHasFixedSize(true)
        linearLayout = androidx.recyclerview.widget.LinearLayoutManager(this)
        mainRecycler.layoutManager = linearLayout
        mainRecycler.clearOnScrollListeners()
        mainRecycler.addOnScrollListener(InfiniteScrollListener({ requestPosts(false) }, linearLayout!!))

        if (mainRecycler.adapter == null) {
            mainRecycler.adapter = MainPostAdapter()
        }

        // more button
        moreButton.setOnClickListener {
            if (filterMenuVisible) {
                moreButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_180_reverse))
                filterMenu.visibility = View.GONE
                filterMenuVisible = false
            } else {
                moreButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_180_normal))
                filterMenu.visibility = View.VISIBLE
                filterMenuVisible = true
            }
        }
        hotButton.setOnClickListener {
            if (postType!=1 && !isRefreshing) {
                hotButton.setTextColor(ContextCompat.getColor(this, R.color.colorOrange))
                hotButton.setTypeface(null, Typeface.BOLD)
                topButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrayDark))
                topButton.setTypeface(null, Typeface.NORMAL)
                newButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrayDark))
                newButton.setTypeface(null, Typeface.NORMAL)
                postType = 1
                moreButton.performClick()
                mSwipe.isRefreshing = true
                requestPosts(true, postType)  // True because we want to fully refresh list
            }
        }

        topButton.setOnClickListener {
            if (!isRefreshing) {


                // select type of Top posts
                val periods = listOf("Hour", "Day", "Week", "Month", "Year", "All")
                selector("Select time period", periods) { _, i ->
                    topType = periods[i].toLowerCase()

                    if (topType != "all") {
                        toast("Showing top posts of this ${periods[i].toLowerCase()}.")
                    } else {
                        toast("Showing top posts of all time.")
                    }

                    topButton.setTextColor(ContextCompat.getColor(this, R.color.colorBlue))
                    topButton.setTypeface(null, Typeface.BOLD)
                    hotButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrayDark))
                    hotButton.setTypeface(null, Typeface.NORMAL)
                    newButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrayDark))
                    newButton.setTypeface(null, Typeface.NORMAL)

                    postType = 2
                    moreButton.performClick()
                    mSwipe.isRefreshing = true
                    requestPosts(true, postType)  // True because we want to fully refresh list
                }
            }
        }

        newButton.setOnClickListener {
            if (postType!=3 && !isRefreshing) {
                newButton.setTextColor(ContextCompat.getColor(this, R.color.colorGreen))
                newButton.setTypeface(null, Typeface.BOLD)
                hotButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrayDark))
                hotButton.setTypeface(null, Typeface.NORMAL)
                topButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrayDark))
                topButton.setTypeface(null, Typeface.NORMAL)
                postType = 3
                moreButton.performClick()
                mSwipe.isRefreshing = true
                requestPosts(true, postType)  // True because we want to fully refresh list
            }
        }

    }

    // Communicate with the ViewModel
    private fun reguestToken() {

        isRefreshing = true
        mSwipe.isRefreshing = true

        mainPresenter.getAccessToken()
    }

    private fun requestPosts(performClear: Boolean, postType: Int = PostType.HOT) {
        this.performClear = performClear
        isRefreshing = true
        mSwipe.isRefreshing = true

        mainPresenter.getPosts(if (performClear) "" else localPosts?.after ?: "", postType, topType)
    }

//        // check if we need to update the access token first
//        val sharedPreferences = getSharedPreferences("reddit_stuff", Context.MODE_PRIVATE)
//        val expiresAtDate = Date(sharedPreferences?.getLong("expires_in_date", Date().time) ?: Date().time)
//        if (expiresAtDate.before(Date()) || expiresAtDate == Date() || TextUtils.isEmpty(sharedPreferences?.getString("access_token", "") ?: "")) {
//            // we need to update the access token
//            reguestToken()
//        } else {
//            isRefreshing = true
//            // if from refresh is true we will ignore the 'after' parameter
//            val subscription = mainPresenter.getPosts(if (fromRefresh) "" else localPosts?.after ?: "", postType, topType)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe (
//                            // onNext()
//                            { retrievedPosts ->
//                                if (!fromRefresh) {
//                                    mSwipe.isRefreshing = false
//                                    (mainRecycler.adapter as MainPostAdapter).addPosts(retrievedPosts.list)
//                                    if (TextUtils.isEmpty(localPosts?.after ?: ""))
//                                        mainRecycler.scrollToPosition(0)
//                                    localPosts = retrievedPosts
//                                    isRefreshing = false
//                                } else {
//                                    // onSwipeRefresh
//                                    mSwipe.isRefreshing = false
//                                    (mainRecycler.adapter as MainPostAdapter).clearAndAddPosts(retrievedPosts.list)
//                                    mainRecycler.scrollToPosition(0)
//                                    localPosts = retrievedPosts
//                                    isRefreshing = false
//                                }
//
//                                mSwipe.isEnabled = true
//
//                                if (linearLayout!=null) {
//                                    mainRecycler.clearOnScrollListeners()
//                                    mainRecycler.addOnScrollListener(InfiniteScrollListener({ requestPosts(false) }, linearLayout!!))
//                                }
//                            },
//                            // onError()
//                            { e ->
//                                mSwipe.isRefreshing = false
//                                isRefreshing = false
//                                Snackbar.make(mainRecycler, e.message ?: "Error during post fetch. Try again later.", Snackbar.LENGTH_LONG).show()
//                            }
//                    )
//
//            // CompositeSubscription to handle onResume/onPause calls
//            subscriptions.add(subscription)

}
