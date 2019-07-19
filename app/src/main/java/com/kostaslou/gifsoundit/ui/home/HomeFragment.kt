package com.kostaslou.gifsoundit.ui.home

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kostaslou.gifsoundit.R
import com.kostaslou.gifsoundit.base.BaseFragment
import com.kostaslou.gifsoundit.commons.GeneralConstants
import com.kostaslou.gifsoundit.commons.PostType
import com.kostaslou.gifsoundit.ui.home.adapter.MainPostAdapter
import com.kostaslou.gifsoundit.util.InfiniteScrollListener
import com.kostaslou.gifsoundit.util.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import javax.inject.Inject


class HomeFragment : BaseFragment() {

    // state
    private var isRefreshing = false

    // ViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: HomeViewModel

    // bind xml
    override fun layoutRes() = R.layout.fragment_home


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel::class.java)

        initUI()
        restoreUI()
//        observeLiveData()
    }

    private fun initUI() {

        val parentActivity = getBaseActivity()

        // toolbar
        parentActivity?.let {
            if (parentActivity.supportActionBar == null) {
                parentActivity.setSupportActionBar(toolbar_top)
            }
        }

        // title font
        parentActivity?.let {
            val typeFace = Typeface.createFromAsset(parentActivity.assets, "fonts/pricedown.ttf")
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
//
//        // slide to refresh
//        mSwipe.isEnabled = false
//        mSwipe.setOnRefreshListener {
//            if (!isRefreshing)
//                requestPosts(true, postType)
//            else
//                mSwipe.isRefreshing = false
//        }
//        mSwipe.setProgressViewOffset(false, 0, 180)
//
        // setup recycler view
        parentActivity?.let {
            mainRecycler.setHasFixedSize(true)

            val linearLayout = LinearLayoutManager(parentActivity)
            mainRecycler.layoutManager = linearLayout
            mainRecycler.clearOnScrollListeners()
            mainRecycler.addOnScrollListener(InfiniteScrollListener({ viewModel.getNextPage() }, linearLayout))

            if (mainRecycler.adapter == null) {
                mainRecycler.adapter = MainPostAdapter()
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

        // todo: maybe remove this if, since events are asynchronous
        if (!isRefreshing) {
            activity?.let {
                when (newPostType) {
                    PostType.HOT -> {
                        hotButton.setTextColor(ContextCompat.getColor(activity, R.color.colorOrange))
                        hotButton.setTypeface(null, Typeface.BOLD)
                        topButton.setTextColor(ContextCompat.getColor(activity, R.color.colorGrayDark))
                        topButton.setTypeface(null, Typeface.NORMAL)
                        newButton.setTextColor(ContextCompat.getColor(activity, R.color.colorGrayDark))
                        newButton.setTypeface(null, Typeface.NORMAL)

                        viewModel.categoryChanged(newPostType)
                    }
                    PostType.NEW -> {
                        newButton.setTextColor(ContextCompat.getColor(activity, R.color.colorGreen))
                        newButton.setTypeface(null, Typeface.BOLD)
                        hotButton.setTextColor(ContextCompat.getColor(activity, R.color.colorGrayDark))
                        hotButton.setTypeface(null, Typeface.NORMAL)
                        topButton.setTextColor(ContextCompat.getColor(activity, R.color.colorGrayDark))
                        topButton.setTypeface(null, Typeface.NORMAL)

                        viewModel.categoryChanged(newPostType)
                    }
                    PostType.TOP -> {

                        // select type of Top posts
                        val periods = listOf("Hour", "Day", "Week", "Month", "Year", "All")
                        activity.selector("Select time period", periods) { _, i ->
                            val topType = periods[i].toLowerCase()

                            if (topType != "all") {
                                activity.toast("Showing top posts of this ${periods[i].toLowerCase()}.")
                            } else {
                                activity.toast("Showing top posts of all time.")
                            }

                            topButton.setTextColor(ContextCompat.getColor(activity, R.color.colorBlue))
                            topButton.setTypeface(null, Typeface.BOLD)
                            hotButton.setTextColor(ContextCompat.getColor(activity, R.color.colorGrayDark))
                            hotButton.setTypeface(null, Typeface.NORMAL)
                            newButton.setTextColor(ContextCompat.getColor(activity, R.color.colorGrayDark))
                            newButton.setTypeface(null, Typeface.NORMAL)

                            viewModel.categoryChanged(newPostType, topType)
                        }
                    }
                }
            }

            if (clickMore)
                moreButton.performClick()
        }
    }

    private fun restoreUI() {
        if (viewModel.filterMenuVisible) {
            activity?.let {
                moreButton.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.rotate_180_normal))
                filterMenu.visibility = View.VISIBLE
            }
        }

        changePostCategory(viewModel.postType, getBaseActivity(), false)
    }
}