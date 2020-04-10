package com.kostaslou.gifsoundit

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.kostaslou.gifsoundit.opengs.OpenGSFragment.Companion.PARAM_QUERY

class Navigator {

    var navController: NavController? = null

    fun bind(navController: NavController) {
        if (this.navController != navController) {
            this.navController = navController
        }
    }

    fun unbind() {
        navController = null
    }

    private fun navigateTo(dest: Destination, bundle: Bundle) {
        navController?.navigate(dest.actionRes, bundle)
    }

    fun navigateToOpenGS(query: String?) {
        navigateTo(Destination.OPENGS, bundleOf(PARAM_QUERY to query))
    }

    enum class Destination(val actionRes: Int) {
        OPENGS(R.id.action_navigate_to_opengs),
    }
}
