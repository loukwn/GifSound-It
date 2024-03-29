package com.loukwn.gifsoundit.navigation

import android.content.Context
import android.view.View
import androidx.navigation.NavController

interface Navigator {
    fun bind(context: Context, navController: NavController)
    fun unbind()

    fun goBack()
    fun clearBackStack()
    fun openShareScreen(query: String)
    fun navigateToOpenGS(
        query: String?,
        fromDeepLink: Boolean,
        containerTransitionView: Pair<View, String>? = null
    )
    fun navigateToSettings()
    fun navigateToOssLicenses()
    fun navigateToCreate()

    fun goToOgWebsite(url: String)

    companion object {
        const val PARAM_OPENGS_QUERY = "Navigator.PARAM_OPENGS_QUERY"
        const val PARAM_OPENGS_FROM_DEEP_LINK = "Navigator.PARAM_OPENGS_FROM_DEEP_LINK"
        const val PARAM_OPENGS_TRANSITION_NAME = "Navigator.PARAM_OPENGS_TRANSITION_NAME"
    }
}
