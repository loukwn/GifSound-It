package com.loukwn.gifsoundit

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.loukwn.gifsoundit.data.storage.SharedPrefsHelper
import com.loukwn.gifsoundit.data.storage.getDayNightMode
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class GifSoundItApp : Application() {

    @Inject
    lateinit var sharedPrefsHelper: SharedPrefsHelper

    override fun onCreate() {
        super.onCreate()
        setSelectedDayNightModeOrSystemDefault()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun setSelectedDayNightModeOrSystemDefault() {
        AppCompatDelegate.setDefaultNightMode(sharedPrefsHelper.getDayNightMode())
    }
}
