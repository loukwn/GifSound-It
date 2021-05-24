package com.kostaslou.gifsoundit.opengs.viewmodel

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.kostaslou.gifsoundit.navigation.Navigator
import com.kostaslou.gifsoundit.opengs.Action
import com.kostaslou.gifsoundit.opengs.GifState
import com.kostaslou.gifsoundit.opengs.OpenGSContract
import com.kostaslou.gifsoundit.opengs.SoundState
import com.kostaslou.gifsoundit.opengs.State
import com.kostaslou.gifsoundit.opengs.UserAction
import com.kostaslou.gifsoundit.opengs.mappers.QueryToStateMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
internal class OpenGSViewModel @Inject constructor(
    private val navigator: Navigator,
    queryToStateMapper: QueryToStateMapper,
    @Named("io") private val ioScheduler: Scheduler,
    @Named("ui") private val uiScheduler: Scheduler,
    private val openGSStateReducer: OpenGSStateReducer,
    private val openGSViewPresenter: OpenGSViewPresenter,
    handle: SavedStateHandle,
) : ViewModel(), OpenGSContract.Listener, OpenGSContract.ViewModel, LifecycleObserver {

    private val query = requireNotNull(handle.get<String>(Navigator.PARAM_OPENGS_QUERY))
    private val isFromDeepLink = handle.get<Boolean>(Navigator.PARAM_OPENGS_FROM_DEEP_LINK) ?: false

    private var view: OpenGSContract.View? = null
    private var disposable: Disposable? = null

    private val actionSubject = PublishSubject.create<Action>()

    private var currentState: State? = null
    private var createdOnce = false

    override fun onCleared() {
        disposable?.dispose()
        disposable = null
        super.onCleared()
    }

    init {
        val initialState = queryToStateMapper.getState(query, isFromDeepLink)
        currentState = initialState

        disposable = actionSubject.scan(initialState) { state, event ->
            val newState = openGSStateReducer.reduce(state, event)
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
        view?.let { openGSViewPresenter.updateView(it, state) }
    }

    override fun onBackButtonPressed() {
        navigator.goBack()
    }

    override fun onRefreshButtonPressed() {
        actionSubject.onNext(Action.OnUserAction(UserAction.ON_REFRESH))
    }

    override fun onShareButtonPressed() {
        navigator.openShareScreen(query)
    }

    override fun onPlayGifLabelPressed() {
        actionSubject.onNext(Action.OnUserAction(UserAction.ON_PLAYGIFLABEL))
    }

    override fun onOffsetIncreaseButtonPressed() {
        actionSubject.onNext(Action.OnUserAction(UserAction.ON_OFFSET_INCREASE))
    }

    override fun onOffsetDecreaseButtonPressed() {
        actionSubject.onNext(Action.OnUserAction(UserAction.ON_OFFSET_DECREASE))
    }

    override fun onOffsetResetButtonPressed() {
        actionSubject.onNext(Action.OnUserAction(UserAction.ON_OFFSET_RESET))
    }

    override fun onGifStateChanged(gifState: GifState) {
        actionSubject.onNext(Action.GifStateChanged(gifState))
    }

    override fun onSoundStateChanged(soundState: SoundState) {
        actionSubject.onNext(Action.SoundStateChanged(soundState))
    }

    override fun onViewWebsiteButtonPressed() {
        navigator.goToOgWebsite(url = query)
    }

    override fun setView(view: OpenGSContract.View) {
        this.view = view
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun doOnCreate() {
        if (createdOnce) {
            actionSubject.onNext(Action.FragmentCreated)
        }
        createdOnce = true
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun doOnStart() {
        view?.setListener(this)
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun doOnStop() {
        view?.removeListener(this)
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun doOnDestroy() {
        view?.release()
    }
}
