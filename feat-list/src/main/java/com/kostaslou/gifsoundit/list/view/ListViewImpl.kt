package com.kostaslou.gifsoundit.list.view

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kostaslou.gifsoundit.common.util.selector
import com.kostaslou.gifsoundit.common.util.tintWithColorRes
import com.kostaslou.gifsoundit.common.util.toast
import com.kostaslou.gifsoundit.list.ListContract
import com.kostaslou.gifsoundit.list.R
import com.kostaslou.gifsoundit.list.databinding.FragmentListBinding
import com.kostaslou.gifsoundit.list.view.adapter.ListAdapterModel
import com.kostaslou.gifsoundit.list.view.adapter.ListPostAdapter
import com.loukwn.postdata.TopFilterType

internal class ListViewImpl(
    private val context: Context,
    inflater: LayoutInflater,
    container: ViewGroup?
) : ListContract.View {

    private val binding = FragmentListBinding.inflate(inflater, container, false)
    private var listener: ListContract.Listener? = null
    private var adapter: ListPostAdapter? = null

    private var infiniteScrollListener: InfiniteScrollListener? = null

    init {
        binding.mSwipe.isEnabled = false
        binding.mSwipe.setOnRefreshListener {
            listener?.onSwipeToRefresh()

        }
        binding.mSwipe.setProgressViewOffset(false, 0, 180)
        binding.loadingScreen.progress.tintWithColorRes(context, R.color.text_primary)

        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.toolbarTitle.setOnClickListener {
            if (binding.mainRecycler.adapter != null) {
                val lManager = binding.mainRecycler.layoutManager as LinearLayoutManager
                if (lManager.findFirstVisibleItemPosition() >= AMOUNT_OF_VIEWS_TO_INSTA_SCROLL) {
                    binding.mainRecycler.scrollToPosition(0)
                } else {
                    binding.mainRecycler.smoothScrollToPosition(0)
                }
            }
        }

        binding.moreButton.setOnClickListener {
            if (binding.filterMenu.isVisible) showFilterMenu() else hideFilterMenu()
            listener?.onMoreMenuButtonClicked()
        }

        binding.hotButton.setOnClickListener { listener?.onHotFilterSelected() }
        binding.newButton.setOnClickListener { listener?.onNewFilterSelected() }
        binding.topButton.setOnClickListener { showSelectorForTypeOptions() }
        binding.settingsBt.setOnClickListener { listener?.onSettingsButtonClicked() }
    }

    private fun showSelectorForTypeOptions() {
        val options = arrayOf(
            TopFilterType.HOUR,
            TopFilterType.DAY,
            TopFilterType.WEEK,
            TopFilterType.MONTH,
            TopFilterType.YEAR,
            TopFilterType.ALL
        )

        context.selector(
            title = context.getString(R.string.home_selector_title),
            options = options.map { context.getString(it.uiLabelRes) }.toTypedArray()
        ) {
            // TODO show a toast as well later
            listener?.onTopFilterSelected(type = options[it])
        }
    }

    private fun setupRecyclerView() {
        binding.mainRecycler.setHasFixedSize(true)

        LinearLayoutManager(context).also { llm ->
            binding.mainRecycler.layoutManager = llm
            binding.mainRecycler.clearOnScrollListeners()

            infiniteScrollListener =
                InfiniteScrollListener({ listener?.onScrolledToBottom() }, llm).also {
                    binding.mainRecycler.addOnScrollListener(it)
                }
        }

        adapter = ListPostAdapter { listener?.onListItemClicked(it) }
        // This will wait until the adapter has data and set the saved state after that
        adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.mainRecycler.adapter = adapter
    }

    override fun allowOrNotSwipeToRefresh(allow: Boolean) {
        if (allow) {
            binding.mSwipe.isEnabled = true
        } else {
            binding.mSwipe.isRefreshing = false
        }
    }

    override fun allowOrNotScrollToBottomLoading(allow: Boolean) {
        infiniteScrollListener?.allowOrNotLoading(allow)
    }

    override fun showList(data: List<ListAdapterModel>) {
        binding.mainRecycler.isVisible = true
        adapter?.submitList(data)
    }

    override fun setLoadingScreenVisibility(isVisible: Boolean) {
        binding.loadingScreen.loadingScreenContainer.isVisible = isVisible
    }

    override fun showFilterMenu() {
        binding.filterMenu.isVisible = true
        binding.moreButton.startAnimation(
            AnimationUtils.loadAnimation(
                context,
                R.anim.rotate_180_normal
            )
        )
    }

    override fun hideFilterMenu() {
        binding.filterMenu.isVisible = false
        binding.moreButton.startAnimation(
            AnimationUtils.loadAnimation(
                context,
                R.anim.rotate_180_reverse
            )
        )
    }

    override fun setFilterMenuToHot() {
        binding.hotButton.setTextColor(ContextCompat.getColor(context, R.color.list_menu_hot))
        binding.hotButton.setTypeface(null, Typeface.BOLD)
        binding.newButton.setTextColor(ContextCompat.getColor(context, R.color.list_menu_inactive))
        binding.newButton.setTypeface(null, Typeface.NORMAL)
        binding.topButton.setTextColor(ContextCompat.getColor(context, R.color.list_menu_inactive))
        binding.topButton.setTypeface(null, Typeface.NORMAL)
    }

    override fun setFilterMenuToNew() {
        binding.hotButton.setTextColor(ContextCompat.getColor(context, R.color.list_menu_inactive))
        binding.hotButton.setTypeface(null, Typeface.NORMAL)
        binding.newButton.setTextColor(ContextCompat.getColor(context, R.color.list_menu_new))
        binding.newButton.setTypeface(null, Typeface.BOLD)
        binding.topButton.setTextColor(ContextCompat.getColor(context, R.color.list_menu_inactive))
        binding.topButton.setTypeface(null, Typeface.NORMAL)
    }

    override fun setFilterMenuToTop() {
        binding.hotButton.setTextColor(ContextCompat.getColor(context, R.color.list_menu_inactive))
        binding.hotButton.setTypeface(null, Typeface.NORMAL)
        binding.newButton.setTextColor(ContextCompat.getColor(context, R.color.list_menu_inactive))
        binding.newButton.setTypeface(null, Typeface.NORMAL)
        binding.topButton.setTextColor(ContextCompat.getColor(context, R.color.list_menu_top))
        binding.topButton.setTypeface(null, Typeface.BOLD)
    }

    override fun showErrorToast(errorMessage: String) {
        context.toast(errorMessage)
    }

    override fun setListener(listener: ListContract.Listener) {
        this.listener = listener
    }

    override fun removeListener(listener: ListContract.Listener) {
        if (this.listener == listener) this.listener = null
    }

    override fun getRoot(): View = binding.root

    companion object {
        private const val AMOUNT_OF_VIEWS_TO_INSTA_SCROLL = 60
    }
}
