package com.kostaslou.gifsoundit

import androidx.navigation.NavController

class Navigator {

    private var mNavController: NavController? = null

    fun bind(navController: NavController) {
        if (mNavController != navController) {
            this.mNavController = navController
        }
    }

    fun unbind() {
        mNavController = null
    }

    val navController get() = mNavController
}
