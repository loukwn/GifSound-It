package com.loukwn.gifsoundit.presentation.common.contract

/**
 * This interface represents Views that can be actioned by user. These have a listener in order to
 * send those action events to their Activity/Fragment
 */
interface ActionableViewContract<ListenerType> : ViewContract {
    fun setListener(listener: ListenerType)
    fun removeListener(listener: ListenerType)
}
