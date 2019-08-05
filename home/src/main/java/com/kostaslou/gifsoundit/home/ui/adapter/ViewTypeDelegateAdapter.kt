package com.kostaslou.gifsoundit.home.ui.adapter

import android.view.ViewGroup

// implemented by all adapters so that they can override their own methods
interface ViewTypeDelegateAdapter {
    fun onCreateViewHolder(parent: ViewGroup): androidx.recyclerview.widget.RecyclerView.ViewHolder
    fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, item: ViewType)
}
