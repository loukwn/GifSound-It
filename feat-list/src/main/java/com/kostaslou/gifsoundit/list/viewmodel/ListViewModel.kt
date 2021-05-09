package com.kostaslou.gifsoundit.list.viewmodel

import android.view.View
import androidx.annotation.RestrictTo
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import com.kostaslou.gifsoundit.common.util.Event
import com.kostaslou.gifsoundit.list.*
import com.kostaslou.gifsoundit.list.Action
import com.kostaslou.gifsoundit.list.FilterType
import com.kostaslou.gifsoundit.list.ListContract
import com.kostaslou.gifsoundit.list.SourceType
import com.kostaslou.gifsoundit.list.State
import com.kostaslou.gifsoundit.list.util.toDTO
import com.kostaslou.gifsoundit.list.view.adapter.ListAdapterModel
import com.loukwn.navigation.Navigator
import com.loukwn.postdata.FilterTypeDTO
import com.loukwn.postdata.PostRepository
import com.loukwn.postdata.TopFilterTypeDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
internal class ListViewModel @Inject constructor(
    private val repository: PostRepository,
    private val navigator: Navigator,
    private val listStateReducer: ListStateReducer,
    private val listViewPresenter: ListViewPresenter,
    @Named("io") ioScheduler: Scheduler,
    @Named("ui") uiScheduler: Scheduler,
) : ViewModel(), LifecycleObserver, ListContract.Listener, ListContract.ViewModel {

    private var disposable: Disposable? = null
    private val actionSubject = PublishSubject.create<Action>()

    private var currentState = State.default()
    private var view: ListContract.View? = null

    @RestrictTo(RestrictTo.Scope.TESTS)
    public override fun onCleared() {
        disposable?.dispose()
        disposable = null
        view = null
        repository.clear()
        super.onCleared()
    }

    init {
        repository.getPosts(
            sourceType = currentState.sourceType.toDTO(),
            filterType = currentState.filterType.toDTO(),
            after = ""
        )

        disposable = Observable.merge(
            onDataChangedEvent(),
            actionSubject
        ).scan(State.default()) { state, event ->
            val newState = listStateReducer.map(state, event)
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
        view?.let { listViewPresenter.updateView(it, state) }
    }

    private fun onDataChangedEvent() = repository.postDataObservable.map { Action.DataChanged(it) }

    override fun onSwipeToRefresh() {
        actionSubject.onNext(Action.SwipedToRefresh)
        repository.getPosts(
            sourceType = currentState.sourceType.toDTO(),
            filterType = currentState.filterType.toDTO(),
            after = ""
        )
    }

    override fun onScrolledToBottom() {
        currentState.fetchAfter?.let {
            repository.getPosts(
                sourceType = currentState.sourceType.toDTO(),
                filterType = currentState.filterType.toDTO(),
                after = it
            )
        }
    }

    override fun onListItemClicked(
        post: ListAdapterModel.Post,
        containerTransitionView: Pair<View, String>
    ) {
        navigator.navigateToOpenGS(
            query = post.url,
            fromDeepLink = false,
            containerTransitionView = containerTransitionView
        )
        view = null
    }

    override fun onSaveButtonClicked(
        selectedSourceType: SourceType,
        selectedFilterType: FilterType
    ) {
        actionSubject.onNext(Action.SaveButtonClicked(selectedSourceType, selectedFilterType))
        repository.getPosts(
            sourceType = selectedSourceType.toDTO(),
            filterType = selectedFilterType.toDTO(),
            after = ""
        )
    }

    override fun onArrowButtonClicked() {
        actionSubject.onNext(Action.ArrowButtonClicked)
    }

    override fun onSettingsButtonClicked() {
        navigator.navigateToSettings()
        view = null
    }

    override fun onOverlayClicked() {
        actionSubject.onNext(Action.OverlayClicked)
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
