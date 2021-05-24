package com.loukwn.gifsoundit.settings.viewmodel

import androidx.annotation.RestrictTo
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import com.loukwn.gifsoundit.common.disk.SharedPrefsHelper
import com.loukwn.gifsoundit.common.disk.getDayNightMode
import com.loukwn.gifsoundit.navigation.Navigator
import com.loukwn.gifsoundit.settings.Action
import com.loukwn.gifsoundit.settings.SettingsContract
import com.loukwn.gifsoundit.settings.State
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
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
    private var createdOnce = false

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
                settingsStateReducer.reduce(state, event)
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
            aboutCollapsed = true,
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

    override fun onAboutBgClicked() {
        actionSubject.onNext(Action.AboutBgClicked)
    }

    override fun onOssContainerClicked() {
        navigator.navigateToOssLicenses()
    }

    override fun setView(view: SettingsContract.View) {
        this.view = view
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun doOnCreate() {
        if (createdOnce) {
            actionSubject.onNext(Action.Created)
        }
        createdOnce = true
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
