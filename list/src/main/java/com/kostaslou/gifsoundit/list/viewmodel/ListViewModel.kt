package com.kostaslou.gifsoundit.list.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import com.kostaslou.gifsoundit.common.util.DataState
import com.kostaslou.gifsoundit.common.util.Event
import com.kostaslou.gifsoundit.list.ListContract
import com.kostaslou.gifsoundit.list.view.adapter.ListAdapterModel
import com.loukwn.postdata.FilterType
import com.loukwn.postdata.PostRepository
import com.loukwn.postdata.TopFilterType
import com.loukwn.postdata.model.domain.PostResponse
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Named

class ListViewModel @ViewModelInject constructor(
    private val repository: PostRepository,
    @Named("io") ioScheduler: Scheduler,
) : ViewModel(), LifecycleObserver, ListContract.Listener, ListContract.ViewModel {

    private val compositeDisposable = CompositeDisposable()

    private var disposable: Disposable? = null
    private val hotFilterSubject = PublishSubject.create<Unit>()
    private val newFilterSubject = PublishSubject.create<Unit>()
    private val topFilterSubject = PublishSubject.create<TopFilterType>()
    private val moreFilterMenuSubject = PublishSubject.create<Unit>()
    private val swipedToRefrehSubject = PublishSubject.create<Unit>()

    private val _navigationEvents = MutableLiveData<Event<NavigationTarget>>()
    val navigationEvents: LiveData<Event<NavigationTarget>> = _navigationEvents

    private var currentState = State.default()
    private var view: ListContract.View? = null
    private var uiHandler: Handler? = null //fixme do i need this

    override fun onCleared() {
        view = null
        uiHandler = null
        compositeDisposable.clear()
        repository.clear()
        super.onCleared()
    }

    init {
        Timber.d("yooo")
        uiHandler = Handler(Looper.getMainLooper())
        repository.getPosts(filterType = currentState.filterType, after = "")

        disposable = Observable.merge(
            listOf(
                onDataChangedEvent(),
                onHotFilterSelectedEvent(),
                onNewFilterSelectedEvent(),
                onTopFilterSelectedEvent(),
                onMoreFilterMenuClickedEvent(),
                onSwipedToRefreshEvent()
            )
        ).scan(State.default()) { state, event ->
            val newState = ListStateReducer.map(state, event)
            Timber.d("$state + $event = $newState")

            uiHandler?.post { ListViewPresenter.updateView(view, state, newState) }

            newState
        }
            .subscribeOn(ioScheduler)
            .subscribe { currentState = it }
    }

    private fun onDataChangedEvent() = repository.postDataObservable.map { Action.DataChanged(it) }
    private fun onHotFilterSelectedEvent() = hotFilterSubject.map { Action.HotFilterSelected }
    private fun onNewFilterSelectedEvent() = newFilterSubject.map { Action.NewFilterSelected }
    private fun onTopFilterSelectedEvent() =
        topFilterSubject.distinctUntilChanged().map { Action.TopFilterSelected(it) }
    private fun onMoreFilterMenuClickedEvent() =
        moreFilterMenuSubject.map { Action.MoreFilterButtonClicked }
    private fun onSwipedToRefreshEvent() = swipedToRefrehSubject.map { Action.SwipedToRefresh }

    override fun onSwipeToRefresh() {
        swipedToRefrehSubject.onNext(Unit)
        repository.getPosts(filterType = currentState.filterType, after = "")
    }

    override fun onScrolledToBottom() {
        currentState.fetchAfter?.let {
            repository.getPosts(
                filterType = currentState.filterType,
                after = it
            )
        }
    }

    override fun onListItemClicked(post: ListAdapterModel.Post) {
        _navigationEvents.postValue(Event(NavigationTarget.OpenGs(gsQuery = post.url)))
    }

    override fun onHotFilterSelected() {
        if (currentState.filterType !is FilterType.Hot) {
            repository.getPosts(filterType = FilterType.Hot, after = "")
            hotFilterSubject.onNext(Unit)
        }
    }

    override fun onNewFilterSelected() {
        if (currentState.filterType != FilterType.New) {
            repository.getPosts(filterType = FilterType.New, after = "")
            newFilterSubject.onNext(Unit)
        }
    }

    override fun onTopFilterSelected(type: TopFilterType) {
        val filterType = currentState.filterType
        if ((filterType is FilterType.Top && filterType.type != type) ||
            filterType !is FilterType.Top
        ) {
            repository.getPosts(filterType = FilterType.Top(type), after = "")
            topFilterSubject.onNext(type)
        }
    }

    override fun onMoreMenuButtonClicked() {
        moreFilterMenuSubject.onNext(Unit)
    }

    override fun setView(view: ListContract.View) {
        this.view = view
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun doOnStart() {
        view?.setListener(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun doOnResume() {
        repository.refreshAuthTokenIfNeeded()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun doOnStop() {
        view?.removeListener(this)
    }

    data class State(
        val adapterData: List<ListAdapterModel>,
        val fetchAfter: String?,
        val isErrored: Boolean,
        val isLoading: Boolean,
        val reachedTheEnd: Boolean,
        val filterMenuIsVisible: Boolean,
        val filterType: FilterType,
    ) {
        companion object {
            fun default() = State(
                adapterData = listOf(ListAdapterModel.Loading),
                fetchAfter = null,
                isErrored = false,
                isLoading = true,
                reachedTheEnd = false,
                filterMenuIsVisible = false,
                filterType = FilterType.Hot
            )
        }

        override fun toString(): String {
            return "AdapterList: ${adapterData.size} isErrored: $isErrored, isLoading: $isLoading, " +
                "filterMenuVisible: $filterMenuIsVisible, filterType: ${filterType.javaClass.simpleName}"
        }
    }

    sealed class Action {
        data class DataChanged(val postResponse: DataState<PostResponse>) : Action()
        object HotFilterSelected : Action()
        object NewFilterSelected : Action()
        data class TopFilterSelected(val topPeriod: TopFilterType) : Action()
        object MoreFilterButtonClicked : Action()
        object SwipedToRefresh: Action()
    }

    sealed class NavigationTarget {
        data class OpenGs(val gsQuery: String) : NavigationTarget()
    }
}
