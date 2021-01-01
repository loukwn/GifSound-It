package com.kostaslou.gifsoundit.list.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import com.kostaslou.gifsoundit.common.util.Event
import com.kostaslou.gifsoundit.list.Action
import com.kostaslou.gifsoundit.list.ListContract
import com.kostaslou.gifsoundit.list.State
import com.kostaslou.gifsoundit.list.view.adapter.ListAdapterModel
import com.loukwn.navigation.Navigator
import com.loukwn.postdata.FilterType
import com.loukwn.postdata.PostRepository
import com.loukwn.postdata.TopFilterType
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Named

class ListViewModel @ViewModelInject constructor(
    private val repository: PostRepository,
    private val navigator: Navigator,
    @Named("io") ioScheduler: Scheduler,
    @Named("ui") uiScheduler: Scheduler,
) : ViewModel(), LifecycleObserver, ListContract.Listener, ListContract.ViewModel {

    private var disposable: Disposable? = null
    private val actionSubject = PublishSubject.create<Action>()

    private var currentState = State.default()
    private var view: ListContract.View? = null

    override fun onCleared() {
        view = null
        repository.clear()
        super.onCleared()
    }

    init {
        repository.getPosts(filterType = currentState.filterType.peekContent(), after = "")

        disposable = Observable.merge(
            onDataChangedEvent(),
            actionSubject
        ).scan(State.default()) { state, event ->
            val newState = ListStateReducer.map(state, event)
            Timber.d("$state + $event = $newState")
            newState
        }
            .subscribeOn(ioScheduler)
            .observeOn(uiScheduler)
            .subscribe { state ->
                // Side effects
                currentState = state
                updateView(state)
            }
    }

    private fun updateView(state: State) {
        view?.let { ListViewPresenter.updateView(it, state) }
    }

    private fun onDataChangedEvent() = repository.postDataObservable.map { Action.DataChanged(it) }

    override fun onSwipeToRefresh() {
        actionSubject.onNext(Action.SwipedToRefresh)
        repository.getPosts(filterType = currentState.filterType.peekContent(), after = "")
    }

    override fun onScrolledToBottom() {
        currentState.fetchAfter?.let {
            repository.getPosts(
                filterType = currentState.filterType.peekContent(),
                after = it
            )
        }
    }

    override fun onListItemClicked(post: ListAdapterModel.Post) {
        navigator.navigateToOpenGS(query = post.url)
    }

    override fun onHotFilterSelected() {
        if (!currentState.filterType.isHot()) {
            repository.getPosts(filterType = FilterType.Hot, after = "")
            actionSubject.onNext(Action.HotFilterSelected)
        }
    }

    override fun onNewFilterSelected() {
        if (!currentState.filterType.isNew()) {
            repository.getPosts(filterType = FilterType.New, after = "")
            actionSubject.onNext(Action.NewFilterSelected)
        }
    }

    override fun onTopFilterSelected(type: TopFilterType) {
        val filterType = currentState.filterType
        if (filterType.isTopButOfDifferentTypeTo(type) || !filterType.isTop()) {
            repository.getPosts(filterType = FilterType.Top(type), after = "")
            actionSubject.onNext(Action.TopFilterSelected(type))
        }
    }

    override fun onMoreMenuButtonClicked() {
        actionSubject.onNext(Action.MoreFilterButtonClicked)
    }

    override fun onSettingsButtonClicked() {
        navigator.navigateToSettings()
    }

    override fun setView(view: ListContract.View) {
        this.view = view
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun doOnCreate() {
        actionSubject.onNext(Action.FragmentCreated)
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
}

fun Event<FilterType>.isHot(): Boolean = this.peekContent() is FilterType.Hot
fun Event<FilterType>.isNew(): Boolean = this.peekContent() is FilterType.New
fun Event<FilterType>.isTop(): Boolean = this.peekContent() is FilterType.Top
fun Event<FilterType>.isTopButOfDifferentTypeTo(type: TopFilterType): Boolean {
    val filterType = this.peekContent()
    return filterType is FilterType.Top && filterType.type != type
}
