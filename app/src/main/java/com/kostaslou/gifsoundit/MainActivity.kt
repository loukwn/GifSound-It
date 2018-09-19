package com.kostaslou.gifsoundit

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
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
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import android.view.animation.AnimationUtils
import com.crashlytics.android.Crashlytics
import com.kostaslou.gifsoundit.commons.GeneralConstants
import io.fabric.sdk.android.Fabric
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity() {

    private var subscriptions = CompositeSubscription()
    private val mainPresenter by lazy { MainPresenter(getSharedPreferences("reddit_stuff", Context.MODE_PRIVATE)) }
    private var localPosts : LocalPostData? = null
    private var accessToken : String? = null
    private var isRefreshing = false
    private var filterMenuVisible = false
    private var postType = 1  // 1 -> HOT, 2 -> TOP, 3 -> NEW
    private var topType = "all"   // hour, day, week etc
    private var stoppedAt : Long? = null

    private var linearLayout: LinearLayoutManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fabric init
        Fabric.with(this, Crashlytics())

        // init
        setContentView(R.layout.activity_main)
        initUI()

        // retain the data during orientation change
        if (savedInstanceState != null && savedInstanceState.containsKey("posts")) {
            localPosts = savedInstanceState.get("posts") as LocalPostData
            (mainRecycler.adapter as MainPostAdapter).clearAndAddPosts(localPosts!!.list)
        }
    }

    override fun onResume() {
        super.onResume()
        subscriptions = CompositeSubscription()

        // check if we need to update the access token
        val sharedPreferences = getSharedPreferences("reddit_stuff", Context.MODE_PRIVATE)
        val expiresAtDate = Date(sharedPreferences?.getLong("expires_in_date", Date().time) ?: Date().time)
        if (expiresAtDate.before(Date()) || expiresAtDate == Date() || TextUtils.isEmpty(sharedPreferences?.getString("access_token", "") ?: "")) {
            // we need to update the access token
            reguestToken()
        } else {

            // if 5 minutes have passed sice we left the activity, or we just started the app -> request posts
            stoppedAt?.let {
                val start = stoppedAt ?: return
                if ( (System.currentTimeMillis()-start) / (1000.0 * 60.0) > GeneralConstants.MINUTES_TO_REFRESH) {
                    // 5 minutes have passed
                    requestPosts(true, postType)
                }
            } ?: requestPosts(true, postType)
        }
    }

    override fun onPause() {
        super.onPause()
        if (!subscriptions.isUnsubscribed) {
            subscriptions.unsubscribe()
        }
        subscriptions.clear()
    }

    override fun onStop() {
        super.onStop()
        stoppedAt = System.currentTimeMillis()
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

    // Util
    private fun initUI() {

        // toolbar
        setSupportActionBar(toolbar_top)

        // title font
        val typeFace = Typeface.createFromAsset(assets, "fonts/pricedown.ttf")
        toolbarTitle.typeface = typeFace
        toolbarTitle.setOnClickListener {
            if (localPosts!=null) mainRecycler.smoothScrollToPosition(0)}

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
        linearLayout = LinearLayoutManager(this)
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
//                    slideFilterMenuUp(true)
                    filterMenu.visibility = View.GONE
                    filterMenuVisible = false
                } else {
                    moreButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_180_normal))
//                    slideFilterMenuUp(false)
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

    // Communicate with presentation layer
    private fun reguestToken() {

        val subscription = mainPresenter.getAccessToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        // onNext()
                        { accessToken ->
                            this.accessToken = accessToken
                            val allOk = (!(TextUtils.isEmpty(accessToken ?: "")))

                            if (allOk) {
                                requestPosts(false, postType)
                            }
                        },
                        // onError()
                        { e ->
                            mSwipe.isRefreshing = false
                            Snackbar.make(mainRecycler, e.message ?: "", Snackbar.LENGTH_LONG).show()
                        }
                )

        // CompositeSubscription to handle onResume/onPause calls
        subscriptions.add(subscription)
    }

    private fun requestPosts(fromRefresh: Boolean, postType: Int = PostType.HOT) {

        isRefreshing = true
        // if from refresh is true we will ignore the 'after' parameter
        val subscription = mainPresenter.getPosts(if (fromRefresh) "" else localPosts?.after ?: "", postType, topType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        // onNext()
                        { retrievedPosts ->
                            if (!fromRefresh) {
                                mSwipe.isRefreshing = false
                                (mainRecycler.adapter as MainPostAdapter).addPosts(retrievedPosts.list)
                                if (TextUtils.isEmpty(localPosts?.after ?: ""))
                                    mainRecycler.scrollToPosition(0)
                                localPosts = retrievedPosts
                                isRefreshing = false
                            } else {
                                // onSwipeRefresh
                                mSwipe.isRefreshing = false
                                (mainRecycler.adapter as MainPostAdapter).clearAndAddPosts(retrievedPosts.list)
                                mainRecycler.scrollToPosition(0)
                                localPosts = retrievedPosts
                                isRefreshing = false
                            }

                            mSwipe.isEnabled = true

                            if (linearLayout!=null) {
                                mainRecycler.clearOnScrollListeners()
                                mainRecycler.addOnScrollListener(InfiniteScrollListener({ requestPosts(false) }, linearLayout!!))
                            }
                        },
                        // onError()
                        { e ->
                            mSwipe.isRefreshing = false
                            isRefreshing = false
                            Snackbar.make(mainRecycler, e.message ?: "", Snackbar.LENGTH_LONG).show()
                        }
                )

        // CompositeSubscription to handle onResume/onPause calls
        subscriptions.add(subscription)
    }
}
