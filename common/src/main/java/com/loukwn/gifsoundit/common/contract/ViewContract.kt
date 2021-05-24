package com.loukwn.gifsoundit.common.contract

import android.os.Bundle
import android.view.View

/**
 *  This is the core functionality of an MvcView. It is used to abstract all ui logic away from
 *  Fragments/Activities, keeping them functioning as a Controller only.
 */

interface ViewContract {

    // The root view that will be inflated for this particular screen
    fun getRoot(): View

    /**
     * Get/Set The state of the view. This can be useful in cases that we need to persist the ui
     * state after a configuration change etc.
     */
    fun getViewState(): Bundle? = null
    fun setViewState(bundle: Bundle) {}
}
