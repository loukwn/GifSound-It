package com.kostaslou.gifsoundit.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

// implemented by all adapters so that they can override their own methods
interface ViewTypeDelegateAdapter {
    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType)
}