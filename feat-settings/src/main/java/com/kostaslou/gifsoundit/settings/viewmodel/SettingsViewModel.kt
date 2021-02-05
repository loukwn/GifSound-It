package com.kostaslou.gifsoundit.settings.viewmodel

import androidx.annotation.RestrictTo
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import com.kostaslou.gifsoundit.common.disk.SharedPrefsHelper
import com.kostaslou.gifsoundit.common.disk.getDayNightMode
import com.kostaslou.gifsoundit.settings.Action
import com.kostaslou.gifsoundit.settings.SettingsContract
import com.kostaslou.gifsoundit.settings.State
import com.loukwn.navigation.Navigator
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import javax.inject.Named

internal class SettingsViewModel @ViewModelInject constructor(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val navigator: Navigator,
    private val settingsStateReducer: SettingsStateReducer,
    private val settingsViewPresenter: SettingsViewPresenter,
    @Named("io") ioScheduler: Scheduler,
    @Named("ui") uiScheduler: Scheduler,
) : ViewModel(), SettingsContract.Listener, SettingsContract.ViewModel, LifecycleObserver {
    private var view: SettingsContract.View? = null

    private var disposable: Disposable? = null
    private val actionSubject = PublishSubject.create<Action>()
    private var currentMode = sharedPrefsHelper.getDayNightMode()

    @RestrictTo(RestrictTo.Scope.TESTS)
    public override fun onCleared() {
        view = null
        disposable?.dispose()
        disposable = null
        super.onCleared()
    }

    init {
        disposable = actionSubject
            .scan(getDefaultState()) { state, event ->
                settingsStateReducer.map(state, event)
            }
            .subscribeOn(ioScheduler)
            .observeOn(uiScheduler)
            .subscribe { state ->
                // Side effects
                updateView(state)
                updateModeIfNeeded(newMode = state.currentMode)
            }
    }

    private fun updateView(state: State) {
        view?.let { settingsViewPresenter.updateView(it, state) }
    }

    private fun updateModeIfNeeded(newMode: Int) {
        if (newMode != currentMode) {
            sharedPrefsHelper.put(SharedPrefsHelper.PREF_KEY_DAYNIGHT_MODE, newMode)
            AppCompatDelegate.setDefaultNightMode(newMode)
            currentMode = newMode
        }
    }

    private fun getDefaultState() =
        State(
            modeSelectorCollapsed = true,
            currentMode = currentMode
        )

    override fun onBackButtonPressed() {
        navigator.goBack()
    }

    override fun onModeSelected(mode: Int) {
        actionSubject.onNext(Action.ModeSelected(mode))
    }

    override fun onModeSelectorBgClicked() {
        actionSubject.onNext(Action.ModeBgClicked)
    }

    override fun setView(view: SettingsContract.View) {
        this.view = view
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun doOnCreate() {
        actionSubject.onNext(Action.Created)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun doOnStart() {
        view?.setListener(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun doOnStop() {
        view?.removeListener(this)
    }
}
