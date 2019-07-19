package com.kostaslou.gifsoundit.util

import androidx.recyclerview.widget.DiffUtil
import com.kostaslou.gifsoundit.util.commons.PostModel

class MyDiffCallback(private val oldList: List<PostModel>, private val newList: List<PostModel>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldList[oldItemPosition].url == newList[newItemPosition].url

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldList[oldItemPosition] == newList[newItemPosition]
}
