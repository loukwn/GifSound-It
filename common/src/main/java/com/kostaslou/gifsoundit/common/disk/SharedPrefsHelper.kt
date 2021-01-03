package com.kostaslou.gifsoundit.common.disk

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import javax.inject.Inject

// Class that deals with the shared preferences operations
class SharedPrefsHelper @Inject constructor(private val mSharedPreferences: SharedPreferences) {

    fun put(key: String, value: String) {
        mSharedPreferences.edit().putString(key, value).apply()
    }

    fun put(key: String, value: Long) {
        mSharedPreferences.edit().putLong(key, value).apply()
    }

    fun put(key: String, value: Int) {
        mSharedPreferences.edit().putInt(key, value).apply()
    }

    operator fun get(key: String, defaultValue: String): String? {
        return mSharedPreferences.getString(key, defaultValue)
    }

    operator fun get(key: String, defaultValue: Int): Int {
        return mSharedPreferences.getInt(key, defaultValue)
    }

    operator fun get(key: String, defaultValue: Float): Float {
        return mSharedPreferences.getFloat(key, defaultValue)
    }

    operator fun get(key: String, defaultValue: Boolean): Boolean {
        return mSharedPreferences.getBoolean(key, defaultValue)
    }

    operator fun get(key: String, defaultValue: Long): Long {
        return mSharedPreferences.getLong(key, defaultValue)
    }

    companion object {
        const val PREF_KEY_ACCESS_TOKEN = "access_token"
        const val PREF_KEY_EXPIRES_AT = "expires_at_date"
        const val PREF_KEY_DAYNIGHT_MODE = "daynight_mode"
    }
}

fun SharedPrefsHelper.getDayNightMode(): Int =
    this[SharedPrefsHelper.PREF_KEY_DAYNIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM]