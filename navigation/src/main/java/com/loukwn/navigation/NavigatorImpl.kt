package com.loukwn.navigation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.loukwn.navigation.Navigator.Companion.PARAM_OPENGS_FROM_DEEP_LINK
import com.loukwn.navigation.Navigator.Companion.PARAM_OPENGS_QUERY
import com.loukwn.navigation.Navigator.Companion.PARAM_OPENGS_TRANSITION_NAME
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NavigatorImpl @Inject constructor() : Navigator {

    private var navController: NavController? = null
    private var context: Context? = null
    private var currentScreen = initialDestination

    override fun bind(context: Context, navController: NavController) {
        this.context = context.activityContext
        if (this.navController != navController) {
            this.navController = navController
        }
    }

    override fun unbind() {
        context = null
        navController = null
    }

    override fun goBack() {
        navController?.navigateUp()
    }

    override fun clearBackStack() {
        navController?.popBackStack()
    }

    override fun openShareScreen(query: String) {
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

    override fun navigateToOpenGS(
        query: String?,
        fromDeepLink: Boolean,
        containerTransitionView: Pair<View, String>?
    ) {
        navigateTo(
            dest = Destination.OPENGS,
            bundle = Bundle().apply {
                putString(PARAM_OPENGS_QUERY, query)
                putBoolean(PARAM_OPENGS_FROM_DEEP_LINK, fromDeepLink)
                if (containerTransitionView != null) {
                    putString(PARAM_OPENGS_TRANSITION_NAME, containerTransitionView.second)
                }
            },
            containerTransitionView = containerTransitionView,
        )
    }

    override fun navigateToSettings() {
        navigateTo(dest = Destination.SETTINGS)
    }

    override fun navigateToOssLicenses() {
        context?.let {
            it.startActivity(Intent(it, OssLicensesMenuActivity::class.java))
        }
    }

    override fun goToOgWebsite(url: String) {
        context?.let { ctx ->
            ctx.launchChooserIntentWithoutThisApp(
                intentBuilder = {
                    Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(url)
                    }
                },
                chooserTitle = "Go to website",
            )
        }
    }

    private fun navigateTo(
        dest: Destination,
        bundle: Bundle? = null,
        containerTransitionView: Pair<View, String>? = null
    ) {
        val extras = containerTransitionView?.let { FragmentNavigatorExtras(it) }
        navController?.navigate(dest.id, bundle, null, extras)
        currentScreen = dest
    }

    enum class Destination(val id: Int) {
        LIST(R.id.fragment_list_id),
        OPENGS(R.id.fragment_opengs_id),
        SETTINGS(R.id.fragment_settings_id),
    }

    companion object {
        private val initialDestination = Destination.LIST
    }
}

private val Context.activityContext: Activity?
    get() {
        var c: Context? = this
        while (c != null && c !is Activity && c is ContextWrapper) {
            c = c.baseContext
        }
        return c as? Activity
    }

private fun Context.launchChooserIntentWithoutThisApp(
    intentBuilder: () -> Intent,
    chooserTitle: String
) {
    val targetedShareIntents: MutableList<Intent> = ArrayList()
    val resInfo = this.packageManager.queryIntentActivities(intentBuilder(), 0)
    if (resInfo.isNotEmpty()) {
        for (info in resInfo) {
            val targetedShare = intentBuilder()
            if (!info.activityInfo.packageName.equals(
                    this.packageName,
                    ignoreCase = true
                )
            ) {
                targetedShare.setPackage(info.activityInfo.packageName)
                targetedShareIntents.add(targetedShare)
            }
        }
        val chooserIntent = Intent.createChooser(
            targetedShareIntents.removeAt(0),
            chooserTitle
        )
        chooserIntent.putExtra(
            Intent.EXTRA_INITIAL_INTENTS,
            targetedShareIntents.toTypedArray()
        )
        this.startActivity(chooserIntent)
    }
}
