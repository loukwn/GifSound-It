package com.kostaslou.gifsoundit

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.kostaslou.gifsoundit.opengs.controller.OpenGSFragment.Companion.PARAM_QUERY

class Navigator {

    private var navController: NavController? = null
    private var currentScreen = Destination.HOME

    fun bind(navController: NavController) {
        if (this.navController != navController) {
            this.navController = navController
        }
    }

    fun unbind() {
        navController = null
    }

    fun navigateToOpenGS(query: String?) {
        navigateTo(Destination.OPENGS, bundleOf(PARAM_QUERY to query))
    }

    fun navigateToSettings() {
        navigateTo(Destination.SETTINGS, null)
    }

    private fun navigateTo(dest: Destination, bundle: Bundle?) {
        navController?.navigate(dest.id, bundle)
        currentScreen = dest
    }

    enum class Destination(val id: Int) {
        HOME(R.id.fragment_home_id),
        OPENGS(R.id.action_home_to_opengs),
        SETTINGS(R.id.fragment_settings_id),
    }
}

