package com.loukwn.navigation

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class Navigator @Inject constructor() {

    private var navController: NavController? = null
    private var currentScreen = initialDestination

    fun bind(navController: NavController) {
        if (this.navController != navController) {
            this.navController = navController
        }
    }

    fun unbind() {
        navController = null
    }

    fun reset() {
        currentScreen = initialDestination
        unbind()
    }

    fun goBack() {
        navController?.navigateUp()
    }

    fun navigateToList(subreddit: String) {
        navigateTo(Destination.LIST, null)
    }

    fun navigateToOpenGS(query: String?) {
        navigateTo(Destination.OPENGS, bundleOf(PARAM_OPENGS_QUERY to query))
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
        SETTINGS(R.id.action_home_to_settings),
    }

    companion object {
        private val initialDestination = Destination.LIST

        const val PARAM_OPENGS_QUERY = "PARAM_OPENGS_QUERY"
    }
}

