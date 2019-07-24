package com.kostaslou.gifsoundit.ui.home

import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kostaslou.gifsoundit.R
import com.kostaslou.gifsoundit.ui.base.BaseFragment
import com.kostaslou.gifsoundit.ui.home.adapter.InfiniteScrollListener
import com.kostaslou.gifsoundit.ui.home.adapter.MainPostAdapter
import com.kostaslou.gifsoundit.ui.home.model.PostModel
import com.kostaslou.gifsoundit.ui.open.OpenGSFragment
import com.kostaslou.gifsoundit.util.ViewModelFactory
import com.kostaslou.gifsoundit.util.commons.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import javax.inject.Inject


class HomeFragment : BaseFragment() {

    private var firstTime = true

    // ViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: HomeViewModel

    // setup ui
    override fun layoutRes() = R.layout.fragment_home

    private lateinit var infiniteScrollListener: InfiniteScrollListener


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel::class.java)

        // inits
        initUI()
        restoreUI()
        resetViewModelMessage()
        observeLiveData()

        // get data for the first time
        viewModel.getPosts(true)
    }

    override fun onResume() {
        super.onResume()
        viewModel.homeResumed()
    }

    private fun initUI() {

        val parentActivity = getBaseActivity()

        // toolbar
        parentActivity?.setSupportActionBar(toolbar_top)

        // title font
        parentActivity?.let {
            val typeFace = Typeface.createFromAsset(it.assets, "fonts/pricedown.ttf")
            toolbarTitle.typeface = typeFace
        }

        // toolbar name on click
        toolbarTitle.setOnClickListener {
            if (mainRecycler.adapter != null) {
                val lManager = mainRecycler.layoutManager as LinearLayoutManager
                if (lManager.findFirstVisibleItemPosition() >= GeneralConstants.AMOUNT_OF_VIEWS_TO_INSTA_SCROLL)
                    mainRecycler.scrollToPosition(0)
                else
                    mainRecycler.smoothScrollToPosition(0)
            }
        }

        // slide to refresh
        mSwipe.isEnabled = false
        mSwipe.setOnRefreshListener {
            viewModel.getPosts(refresh = true)
        }
        mSwipe.setProgressViewOffset(false, 0, 180)

        // setup recycler view
        parentActivity?.let { act ->
            mainRecycler.setHasFixedSize(true)

            val linearLayout = LinearLayoutManager(act)
            mainRecycler.layoutManager = linearLayout
            mainRecycler.clearOnScrollListeners()

            infiniteScrollListener = InfiniteScrollListener({ viewModel.getPosts() }, linearLayout)
            mainRecycler.addOnScrollListener(infiniteScrollListener)

            if (mainRecycler.adapter == null) {
                mainRecycler.adapter = MainPostAdapter {
                    // when a list item is clicked...

                    // get the query part of the link
                    val partsOfLink = it.url.split("?")
                    val query = if (partsOfLink.size > 1) {
                        var temp = ""
                        for (i in 1 until partsOfLink.size)
                            temp += "?" + partsOfLink[i]
                        temp.substring(1)
                    } else {
                        null
                    }

                    // open the gifsound...
                    navigateToOpenGSFragment(query)
                }
            }
        }

        // more button on click
        parentActivity?.let {
            moreButton.setOnClickListener {

                viewModel.moreClicked()
                val filterMenuVisible = viewModel.filterMenuVisible

                if (filterMenuVisible) {
                    moreButton.startAnimation(AnimationUtils.loadAnimation(parentActivity, R.anim.rotate_180_normal))
                    filterMenu.visibility = View.VISIBLE
                } else {
                    moreButton.startAnimation(AnimationUtils.loadAnimation(parentActivity, R.anim.rotate_180_reverse))
                    filterMenu.visibility = View.GONE
                }
            }
        }

        // post category change
        hotButton.setOnClickListener {
            changePostCategory(PostType.HOT, parentActivity)
        }

        topButton.setOnClickListener {
            changePostCategory(PostType.TOP, parentActivity)
        }

        newButton.setOnClickListener {
            changePostCategory(PostType.NEW, parentActivity)
        }
    }

    private fun changePostCategory(newPostType: PostType, activity: AppCompatActivity?, clickMore: Boolean = true) {

        if (!mSwipe.isRefreshing) {
            activity?.let {
                when (newPostType) {
                    PostType.HOT -> {

                        changeFilterLabelColors(newPostType, activity)
                        viewModel.categoryChanged(newPostType)
                    }
                    PostType.NEW -> {

                        changeFilterLabelColors(newPostType, activity)
                        viewModel.categoryChanged(newPostType)
                    }
                    PostType.TOP -> {

                        // select type of Top posts
                        val periods = listOf(getString(R.string.home_top_hour), getString(R.string.home_top_day),
                                getString(R.string.home_top_week), getString(R.string.home_top_month),
                                getString(R.string.home_top_year), getString(R.string.home_top_all))

                        activity.selector(getString(R.string.home_selector_title), periods) { _, i ->
                            val topType = periods[i].toLowerCase()

                            if (!TextUtils.equals(topType, getString(R.string.home_top_all).toLowerCase())) {
                                activity.toast(getString(R.string.home_selector_other_chosen, periods[i].toLowerCase()))
                            } else {
                                activity.toast(getString(R.string.home_selector_all_chosen))
                            }

                            changeFilterLabelColors(newPostType, activity)
                            viewModel.categoryChanged(newPostType, topType)
                        }
                    }
                }
            }

            if (clickMore)
                moreButton.performClick()
        }
    }

    private fun changeFilterLabelColors(postType: PostType, activity: AppCompatActivity) {
        when (postType) {
            PostType.HOT -> {
                hotButton.setTextColor(ContextCompat.getColor(activity, R.color.colorOrange))
                hotButton.setTypeface(null, Typeface.BOLD)
                newButton.setTextColor(ContextCompat.getColor(activity, R.color.colorGrayDark))
                newButton.setTypeface(null, Typeface.NORMAL)
                topButton.setTextColor(ContextCompat.getColor(activity, R.color.colorGrayDark))
                topButton.setTypeface(null, Typeface.NORMAL)
            }
            PostType.NEW -> {
                hotButton.setTextColor(ContextCompat.getColor(activity, R.color.colorGrayDark))
                hotButton.setTypeface(null, Typeface.NORMAL)
                newButton.setTextColor(ContextCompat.getColor(activity, R.color.colorGreen))
                newButton.setTypeface(null, Typeface.BOLD)
                topButton.setTextColor(ContextCompat.getColor(activity, R.color.colorGrayDark))
                topButton.setTypeface(null, Typeface.NORMAL)
            }
            PostType.TOP -> {
                hotButton.setTextColor(ContextCompat.getColor(activity, R.color.colorGrayDark))
                hotButton.setTypeface(null, Typeface.NORMAL)
                newButton.setTextColor(ContextCompat.getColor(activity, R.color.colorGrayDark))
                newButton.setTypeface(null, Typeface.NORMAL)
                topButton.setTextColor(ContextCompat.getColor(activity, R.color.colorBlue))
                topButton.setTypeface(null, Typeface.BOLD)
            }
        }
    }

    private fun restoreUI() {

        getBaseActivity()?.let {
            // filter menu
            if (viewModel.filterMenuVisible) {
                moreButton.startAnimation(AnimationUtils.loadAnimation(it, R.anim.rotate_180_normal))
                filterMenu.visibility = View.VISIBLE
            }

            // filter selection
            changeFilterLabelColors(viewModel.postType, it)
        }
    }

    private fun resetViewModelMessage() {
        viewModel.resetMessage()
    }

    private fun observeLiveData() {
        viewModel.postsLiveData.observe(this, Observer<List<PostModel>> {
            // viewmodel has some data for us

            (mainRecycler.adapter as MainPostAdapter).clearAndAddPosts(it)
            mSwipe.isEnabled = true
            firstTime = false
            infiniteScrollListener.allowLoading()
        })

        viewModel.loadingLiveData.observe(this, Observer<Boolean> {
            // viewmodel indicates that data is loading

            mSwipe.isRefreshing = it
        })

        viewModel.messageLiveData.observe(this, Observer<Message> {
            // viewmodel wants to convey a message to us

            mSwipe.isEnabled = true

            var errorText : String? = null

            when (it) {
                is Message.Error -> {
                    errorText = when (it.e) {
                        is PostsHttpException -> getString(R.string.home_error_posts)
                        is TokenHttpException -> getString(R.string.home_error_token)
                        is TokenRequiredException -> getString(R.string.home_error_token_refresh)
                        else -> getString(R.string.home_error_generic)
                    }
                }
                is Message.Info -> {
                    when (it.data) {
                        MessageCodes.TOKEN_READY -> {
                            errorText = getString(R.string.home_info_token_ready)
                            if (firstTime) viewModel.getPosts(firstTime)
                        }
                        MessageCodes.RECREATED -> {
                            firstTime = false
                        }
                    }
                }
            }

            errorText?.let {Snackbar.make(mainRecycler, errorText, Snackbar.LENGTH_SHORT).show()}

            infiniteScrollListener.allowLoading()
        })
    }

    private fun navigateToOpenGSFragment(query: String?) {

        val args = Bundle()
        args.putString("query", query)

        val frag = OpenGSFragment()
        frag.arguments = args

        getBaseActivity()?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragContainer, frag)
                ?.addToBackStack(null)?.commit()
    }
}