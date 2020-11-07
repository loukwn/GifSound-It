package com.kostaslou.gifsoundit.home.ui

import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kostaslou.gifsoundit.home.R
import com.kostaslou.gifsoundit.home.ui.adapter.InfiniteScrollListener
import com.kostaslou.gifsoundit.home.ui.adapter.MainPostAdapter
import com.kostaslou.gifsoundit.home.util.ViewModelFactory
import com.kostaslou.gifsoundit.home.util.commons.GeneralConstants
import com.kostaslou.gifsoundit.home.util.commons.Message
import com.kostaslou.gifsoundit.home.util.commons.MessageCodes
import com.kostaslou.gifsoundit.home.util.commons.PostType
import com.kostaslou.gifsoundit.home.util.commons.PostsHttpException
import com.kostaslou.gifsoundit.home.util.commons.TokenHttpException
import com.kostaslou.gifsoundit.home.util.commons.TokenRequiredException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var firstTime = true

    // ViewModel
    private val viewModel: HomeViewModel by viewModels()
    // private val viewModel: HomeViewModel by navGraphViewModels(R.id.) {
    //     defaultViewModelProviderFactory
    // }

    // custom scroller for the infinite recyclerview
    private lateinit var infiniteScrollListener: InfiniteScrollListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    // lifecycle stuff
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        // toolbar
        // activity?.setSupportActionBar(toolbar_top)

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
        activity?.let { act ->
            mainRecycler.setHasFixedSize(true)

            val linearLayout = LinearLayoutManager(act)
            mainRecycler.layoutManager = linearLayout
            mainRecycler.clearOnScrollListeners()

            infiniteScrollListener =
                InfiniteScrollListener({ viewModel.getPosts() }, linearLayout)
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
        activity?.let {
            moreButton.setOnClickListener { view ->

                viewModel.moreClicked()
                val filterMenuVisible = viewModel.getFilterMenuVisible()

                if (filterMenuVisible) {
                    moreButton.startAnimation(AnimationUtils.loadAnimation(view.context, R.anim.rotate_180_normal))
                    filterMenu.visibility = View.VISIBLE
                } else {
                    moreButton.startAnimation(AnimationUtils.loadAnimation(view.context, R.anim.rotate_180_reverse))
                    filterMenu.visibility = View.GONE
                }
            }
        }

        // post category change
        hotButton.setOnClickListener {
            changePostCategory(PostType.HOT)
        }

        topButton.setOnClickListener {
            changePostCategory(PostType.TOP)
        }

        newButton.setOnClickListener {
            changePostCategory(PostType.NEW)
        }
    }

    private fun changePostCategory(newPostType: PostType, clickMore: Boolean = true) {

        if (!mSwipe.isRefreshing) {
            context?.let {
                when (newPostType) {
                    PostType.HOT -> {

                        changeFilterLabelColors(newPostType)
                        viewModel.categoryChanged(newPostType)
                    }
                    PostType.NEW -> {

                        changeFilterLabelColors(newPostType)
                        viewModel.categoryChanged(newPostType)
                    }
                    PostType.TOP -> {

                        // select type of Top posts
                        val periods = listOf(getString(R.string.home_top_hour), getString(R.string.home_top_day),
                                getString(R.string.home_top_week), getString(R.string.home_top_month),
                                getString(R.string.home_top_year), getString(R.string.home_top_all))

                        it.selector(getString(R.string.home_selector_title), periods) { _, i ->
                            val topType = periods[i].toLowerCase(Locale.ROOT)

                            if (!TextUtils.equals(topType, getString(R.string.home_top_all).toLowerCase(Locale.ROOT))) {
                                it.toast(getString(R.string.home_selector_other_chosen, periods[i].toLowerCase(Locale.getDefault())))
                            } else {
                                it.toast(getString(R.string.home_selector_all_chosen))
                            }

                            changeFilterLabelColors(newPostType)
                            viewModel.categoryChanged(newPostType, topType)
                        }
                    }
                }
            }

            if (clickMore)
                moreButton.performClick()
        }
    }

    private fun changeFilterLabelColors(postType: PostType) {
        context?.let { ctx ->
            when (postType) {
                PostType.HOT -> {
                    hotButton.setTextColor(ContextCompat.getColor(ctx, R.color.colorOrange))
                    hotButton.setTypeface(null, Typeface.BOLD)
                    newButton.setTextColor(ContextCompat.getColor(ctx, R.color.colorGrayDark))
                    newButton.setTypeface(null, Typeface.NORMAL)
                    topButton.setTextColor(ContextCompat.getColor(ctx, R.color.colorGrayDark))
                    topButton.setTypeface(null, Typeface.NORMAL)
                }
                PostType.NEW -> {
                    hotButton.setTextColor(ContextCompat.getColor(ctx, R.color.colorGrayDark))
                    hotButton.setTypeface(null, Typeface.NORMAL)
                    newButton.setTextColor(ContextCompat.getColor(ctx, R.color.colorGreen))
                    newButton.setTypeface(null, Typeface.BOLD)
                    topButton.setTextColor(ContextCompat.getColor(ctx, R.color.colorGrayDark))
                    topButton.setTypeface(null, Typeface.NORMAL)
                }
                PostType.TOP -> {
                    hotButton.setTextColor(ContextCompat.getColor(ctx, R.color.colorGrayDark))
                    hotButton.setTypeface(null, Typeface.NORMAL)
                    newButton.setTextColor(ContextCompat.getColor(ctx, R.color.colorGrayDark))
                    newButton.setTypeface(null, Typeface.NORMAL)
                    topButton.setTextColor(ContextCompat.getColor(ctx, R.color.colorBlue))
                    topButton.setTypeface(null, Typeface.BOLD)
                }
            }
        }
    }

    private fun restoreUI() {

        activity?.let {
            // filter menu
            if (viewModel.getFilterMenuVisible()) {
                moreButton.startAnimation(AnimationUtils.loadAnimation(it, R.anim.rotate_180_normal))
                filterMenu.visibility = View.VISIBLE
            }

            // filter selection
            changeFilterLabelColors(viewModel.getPostType())
        }
    }

    private fun resetViewModelMessage() {
        viewModel.resetMessage()
    }

    private fun observeLiveData() {
        viewModel.postsLiveData.observe(viewLifecycleOwner, {
            // viewmodel has some data for us

            (mainRecycler.adapter as MainPostAdapter).clearAndAddPosts(it)
            mSwipe.isEnabled = true
            firstTime = false
            infiniteScrollListener.allowLoading()
        })

        viewModel.loadingLiveData.observe(viewLifecycleOwner, {
            // viewmodel indicates that data is loading

            mSwipe.isRefreshing = it
        })

        viewModel.messageLiveData.observe(viewLifecycleOwner, {
            // viewmodel wants to convey a message to us

            mSwipe.isEnabled = true

            var errorText: String? = null

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

            errorText?.let { Snackbar.make(mainRecycler, errorText, Snackbar.LENGTH_SHORT).show() }

            infiniteScrollListener.allowLoading()
        })
    }

    private fun navigateToOpenGSFragment(query: String?) {
        (activity as? Callback)?.navigateToOpenGS(query)
    }

    interface Callback {
        fun navigateToOpenGS(query: String?)
    }
}
