package com.kostaslou.gifsoundit.common

/**
 * This interface represents Views that can be actioned by user. These have a listener in order to
 * send those action events to their Activity/Fragment
 */
interface ActionableViewMvc<ListenerType>: ViewMvc {
    fun setListener(listener: ListenerType)
    fun removeListener(listener: ListenerType)
}
