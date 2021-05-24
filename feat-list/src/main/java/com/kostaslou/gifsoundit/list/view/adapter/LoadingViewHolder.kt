package com.kostaslou.gifsoundit.list.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kostaslou.gifsoundit.common.util.tintWithColorRes
import com.kostaslou.list.R
import com.kostaslou.list.databinding.ItemLoadingBinding

class LoadingViewHolder(
    binding: ItemLoadingBinding,
) : RecyclerView.ViewHolder(binding.root) {
    constructor(
        inflater: LayoutInflater,
        parent: ViewGroup?,
    ) : this(ItemLoadingBinding.inflate(inflater, parent, false))

    init {
        binding.progress.tintWithColorRes(itemView.context, R.color.text_primary)
    }
}
