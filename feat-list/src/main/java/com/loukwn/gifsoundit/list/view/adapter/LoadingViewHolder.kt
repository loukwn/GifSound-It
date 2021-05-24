package com.loukwn.gifsoundit.list.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.loukwn.gifsoundit.common.util.tintWithColorRes
import com.loukwn.gifsoundit.list.R
import com.loukwn.gifsoundit.list.databinding.ItemLoadingBinding

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
