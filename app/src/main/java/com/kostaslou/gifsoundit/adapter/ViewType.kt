package com.kostaslou.gifsoundit.adapter

// this interface will be implemented by every item of the recycler list so that we know whether
// the item is a post or the loading one
interface ViewType {
    fun getViewType(): Int
}