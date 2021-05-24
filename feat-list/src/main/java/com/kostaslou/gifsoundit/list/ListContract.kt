package com.kostaslou.gifsoundit.list

import android.view.View
import androidx.annotation.IdRes
import com.kostaslou.gifsoundit.common.contract.ActionableViewContract
import com.kostaslou.gifsoundit.common.util.DataState
import com.kostaslou.gifsoundit.common.util.Event
import com.kostaslou.gifsoundit.list.view.adapter.ListAdapterModel
import com.kostaslou.gifsoundit.postdata.model.domain.PostResponse
import com.kostaslou.list.R

internal interface ListContract {

    interface View : ActionableViewContract<Listener> {
        fun allowOrNotSwipeToRefresh(allow: Boolean)
        fun allowOrNotScrollToBottomLoading(allow: Boolean)
        fun showList(data: List<ListAdapterModel>)
        fun setLoadingScreenVisibility(isVisible: Boolean)
        fun showOptionsLayout(sourceType: SourceType, filterType: FilterType)
        fun hideOptionsLayout()
        fun showOverlay()
        fun hideOverlay()
        fun showErrorToast(errorMessage: String)
    }

    interface Listener {
        fun onSwipeToRefresh()
        fun onScrolledToBottom()
        fun onListItemClicked(
            post: ListAdapterModel.Post,
            containerTransitionView: Pair<android.view.View, String>
        )

        fun onSaveButtonClicked(
            selectedSourceType: SourceType,
            selectedFilterType: FilterType,
        )

        fun onArrowButtonClicked()
        fun onSettingsButtonClicked()
        fun onOverlayClicked()
    }

    interface ViewModel {
        fun setView(view: View)
        fun onBackPressed(): Boolean
    }
}

internal enum class FilterType(@IdRes val chipId: Int) {
    Hot(chipId = R.id.chipHot),
    New(chipId = R.id.chipNew),
    TopHour(chipId = R.id.chipTopHour),
    TopDay(chipId = R.id.chipTopDay),
    TopWeek(chipId = R.id.chipTopWeek),
    TopMonth(chipId = R.id.chipTopMonth),
    TopYear(chipId = R.id.chipTopYear),
    TopAll(chipId = R.id.chipTopAll),
}

internal enum class SourceType(@IdRes val chipId: Int) {
    GifSound(R.id.chipGifsound),
    AnimeGifSound(R.id.chipAnimeGifSound),
    MusicGifStation(R.id.chipMusicGifStation),
}

internal data class State(
    val adapterData: List<ListAdapterModel>,
    val fetchAfter: String?,
    val errorMessage: Event<String?>?,
    val isLoading: Boolean,
    val optionsLayoutIsOpen: Boolean,
    val filterType: FilterType,
    val sourceType: SourceType,
) {
    companion object {
        fun default() = State(
            adapterData = emptyList(),
            fetchAfter = null,
            errorMessage = null,
            isLoading = true,
            optionsLayoutIsOpen = false,
            filterType = FilterType.Hot,
            sourceType = SourceType.GifSound,
        )
    }

    override fun toString(): String {
        return "AdapterList: ${adapterData.size}, isErrored: ${errorMessage?.peekContent()}," +
            " isLoading: $isLoading, filterType: ${filterType.javaClass.simpleName}"
    }
}

internal sealed class Action {
    data class DataChanged(val postResponse: DataState<PostResponse>) : Action() {
        override fun toString(): String {
            return if (postResponse is DataState.Data) {
                "DataChanged.Data with size: ${postResponse()?.postData?.size}"
            } else super.toString()
        }
    }

    data class SaveButtonClicked(val sourceType: SourceType, val filterType: FilterType) : Action()
    object OverlayClicked : Action()
    object ArrowButtonClicked : Action()
    object SwipedToRefresh : Action()
    object FragmentCreated : Action()
    object OnBackPressed : Action()
}

internal sealed class NavigationAction {
    data class OpenGs(
        val query: String,
        val containerTransitionView: Pair<View, String>
    ) : NavigationAction()

    object Settings : NavigationAction()
}
