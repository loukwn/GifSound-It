package com.loukwn.gifsoundit.list.view

import android.animation.LayoutTransition
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loukwn.gifsoundit.common.util.tintWithColorRes
import com.loukwn.gifsoundit.common.util.toast
import com.loukwn.gifsoundit.list.FilterType
import com.loukwn.gifsoundit.list.ListContract
import com.loukwn.gifsoundit.list.R
import com.loukwn.gifsoundit.list.SourceType
import com.loukwn.gifsoundit.list.databinding.FragmentListBinding
import com.loukwn.gifsoundit.list.view.adapter.ListAdapterModel
import com.loukwn.gifsoundit.list.view.adapter.ListPostAdapter

internal class ListViewImpl(
    private val context: Context,
    inflater: LayoutInflater,
    container: ViewGroup?,
    private val onRecyclerViewPopulated: () -> Unit,
) : ListContract.View {

    private val binding = FragmentListBinding.inflate(inflater, container, false)
    private var listener: ListContract.Listener? = null
    private var adapter: ListPostAdapter? = null

    private var infiniteScrollListener: InfiniteScrollListener? = null

    init {
        binding.mSwipe.isEnabled = false
        binding.mSwipe.setOnRefreshListener { listener?.onSwipeToRefresh() }
        binding.mSwipe.setProgressViewOffset(false, 0, 180)
        binding.loadingScreen.progress.tintWithColorRes(context, R.color.text_primary)

        setupOptionLayoutTransition()
        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupOptionLayoutTransition() {
        val interpolator = OvershootInterpolator(2f)
        val transition = LayoutTransition().apply {
            setInterpolator(LayoutTransition.CHANGE_APPEARING, interpolator)
            setInterpolator(LayoutTransition.CHANGE_DISAPPEARING, interpolator)
            setInterpolator(LayoutTransition.APPEARING, interpolator)
            setInterpolator(LayoutTransition.DISAPPEARING, interpolator)
            setDuration(OPTIONS_LAYOUT_SHOW_HIDE_DURATION_MS)
        }

        binding.root.layoutTransition = transition
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

        binding.moreButton.setOnClickListener { listener?.onArrowButtonClicked() }
        binding.darkOverlay.setOnClickListener { listener?.onOverlayClicked() }
        binding.settingsBt.setOnClickListener { listener?.onSettingsButtonClicked() }
        binding.optionsLayout.saveButton.setOnClickListener {
            val selectedSourceType = SourceType.values()
                .first { it.chipId == binding.optionsLayout.sourceChipGroup.checkedChipId }
            val selectedFilterType = FilterType.values()
                .first { it.chipId == binding.optionsLayout.filterChipGroup.checkedChipId }

            listener?.onSaveButtonClicked(selectedSourceType, selectedFilterType)
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

        adapter = ListPostAdapter { post, containerTransitionView -> listener?.onListItemClicked(post, containerTransitionView) }
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
        adapter?.submitList(data, onRecyclerViewPopulated)
    }

    override fun setLoadingScreenVisibility(isVisible: Boolean) {
        binding.loadingScreen.loadingScreenContainer.isVisible = isVisible
    }

    override fun showOptionsLayout(sourceType: SourceType, filterType: FilterType) {
        binding.optionsLayout.sourceChipGroup.check(sourceType.chipId)
        binding.optionsLayout.filterChipGroup.check(filterType.chipId)
        binding.optionsLayout.root.isVisible = true
    }

    override fun hideOptionsLayout() {
        binding.optionsLayout.root.isVisible = false
    }

    override fun showOverlay() {
        binding.darkOverlay.isVisible = true
    }

    override fun hideOverlay() {
        binding.darkOverlay.isVisible = false
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
        private const val OPTIONS_LAYOUT_SHOW_HIDE_DURATION_MS = 200L
    }
}
