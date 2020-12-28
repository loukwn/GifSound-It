package com.kostaslou.gifsoundit

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.kostaslou.gifsoundit.opengs.controller.OpenGSFragment.Companion.PARAM_QUERY

class Navigator {

    private var navController: NavController? = null
    private var currentScreen = Destination.LIST

    fun bind(navController: NavController) {
        if (this.navController != navController) {
            this.navController = navController
        }
    }

    fun unbind() {
        navController = null
    }

    fun navigateToList(subreddit: String) {
        navigateTo(Destination.LIST, null)
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
        LIST(R.id.fragment_list_id),
        OPENGS(R.id.action_home_to_opengs),
        SETTINGS(R.id.fragment_settings_id),
    }
}

