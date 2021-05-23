package com.kostaslou.gifsoundit.list.viewmodel

import android.view.View
import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import com.kostaslou.gifsoundit.list.Action
import com.kostaslou.gifsoundit.list.FilterType
import com.kostaslou.gifsoundit.list.ListContract
import com.kostaslou.gifsoundit.list.NavigationAction
import com.kostaslou.gifsoundit.list.SourceType
import com.kostaslou.gifsoundit.list.State
import com.kostaslou.gifsoundit.list.util.toDTO
import com.kostaslou.gifsoundit.list.view.adapter.ListAdapterModel
import com.loukwn.navigation.Navigator
import com.loukwn.postdata.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit
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
    @Named("computation") computationScheduler: Scheduler,
) : ViewModel(), LifecycleObserver, ListContract.Listener, ListContract.ViewModel {

    private var disposable: CompositeDisposable? = null
    private val actionSubject = PublishSubject.create<Action>()
    private val throttledNavigationActionSubject = PublishSubject.create<NavigationAction>()

    private var currentState = State.default()
    private var view: ListContract.View? = null
    private var createdOnce = false

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

        disposable = CompositeDisposable()
        disposable?.addAll(
            Observable.merge(
                onDataChangedEvent(),
                actionSubject
            ).scan(State.default()) { state, event ->
                val newState = listStateReducer.reduce(state, event)
                Timber.d("$state + $event = $newState")
                newState
            }
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe { state ->
                    // Side effects
                    currentState = state
                    updateView(state)
                },

            throttledNavigationActionSubject
                .throttleFirst(THROTTLE_WINDOW_NAVIGATION_ACTION_MS, TimeUnit.MILLISECONDS, computationScheduler)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe { action ->
                    when (action) {
                        is NavigationAction.OpenGs -> {
                            navigator.navigateToOpenGS(
                                query = action.query,
                                fromDeepLink = false,
                                containerTransitionView = action.containerTransitionView
                            )
                        }
                        NavigationAction.Settings -> {
                            navigator.navigateToSettings()
                        }
                    }
                    view = null
                }
        )
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
        throttledNavigationActionSubject.onNext(
            NavigationAction.OpenGs(
                post.url,
                containerTransitionView
            )
        )
    }

    override fun onSaveButtonClicked(
        selectedSourceType: SourceType,
        selectedFilterType: FilterType
    ) {
        actionSubject.onNext(Action.SaveButtonClicked(selectedSourceType, selectedFilterType))
        if (currentState.filterType != selectedFilterType || currentState.sourceType != selectedSourceType) {
            repository.getPosts(
                sourceType = selectedSourceType.toDTO(),
                filterType = selectedFilterType.toDTO(),
                after = ""
            )
        }
    }

    override fun onArrowButtonClicked() {
        actionSubject.onNext(Action.ArrowButtonClicked)
    }

    override fun onSettingsButtonClicked() {
        throttledNavigationActionSubject.onNext(NavigationAction.Settings)
    }

    override fun onOverlayClicked() {
        actionSubject.onNext(Action.OverlayClicked)
    }

    override fun setView(view: ListContract.View) {
        this.view = view
    }

    override fun onBackPressed(): Boolean {
        return if (currentState.optionsLayoutIsOpen) {
            actionSubject.onNext(Action.OnBackPressed)
            true
        } else {
            false
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun doOnCreate() {
        if (createdOnce) {
            actionSubject.onNext(Action.FragmentCreated)
        }
        createdOnce = true
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

    companion object {
        @VisibleForTesting
        internal const val THROTTLE_WINDOW_NAVIGATION_ACTION_MS = 150L
    }
}
