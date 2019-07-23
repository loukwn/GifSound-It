package com.kostaslou.gifsoundit.util

import androidx.recyclerview.widget.DiffUtil
import com.kostaslou.gifsoundit.ui.home.adapter.ViewType
import com.kostaslou.gifsoundit.ui.home.model.PostModel
import com.kostaslou.gifsoundit.util.commons.AdapterConstants

@Suppress("unused")
// todo change to this instead of notifyDatasetChanged
class MyDiffCallback(private val oldList: List<ViewType>, private val newList: List<ViewType>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) : Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]

        val oldType = old.getViewType()
        val newType = new.getViewType()

        return if (oldType == newType && oldType == AdapterConstants.LOADING) true
        else if (oldType == AdapterConstants.LOADING || newType == AdapterConstants.LOADING) false
        else (old as PostModel).url == (new as PostModel).url
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldList[oldItemPosition] == newList[newItemPosition]
}
