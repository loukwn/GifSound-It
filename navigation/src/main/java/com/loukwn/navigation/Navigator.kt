package com.loukwn.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Navigator @Inject constructor() {

    private var navController: NavController? = null
    private var context: Context? = null
    private var currentScreen = initialDestination

    fun bind(context: Context, navController: NavController) {
        this.context = context
        if (this.navController != navController) {
            this.navController = navController
        }
    }

    fun unbind() {
        context = null
        navController = null
    }

    fun goBack() {
        navController?.navigateUp()
    }

    fun openShareScreen(query: String) {
        context?.let {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, query)
                type = "text/plain"
            }

            it.startActivity(
                Intent.createChooser(
                    sendIntent,
                    "asd"
                )
            )
        }
    }

    fun navigateToOpenGS(query: String?, fromDeepLink: Boolean) {
        navigateTo(
            Destination.OPENGS,
            bundleOf(
                PARAM_OPENGS_QUERY to query,
                PARAM_OPENGS_FROM_DEEP_LINK to fromDeepLink
            )
        )
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
        const val PARAM_OPENGS_FROM_DEEP_LINK = "PARAM_OPENGS_FROM_DEEP_LINK"
    }
}

