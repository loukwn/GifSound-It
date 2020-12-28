package com.kostaslou.gifsoundit.list.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ListPostAdapter(
    private val onItemClicked: (ListAdapterModel.Post) -> Unit
) : ListAdapter<ListAdapterModel, RecyclerView.ViewHolder>(Differ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_LOADING -> LoadingViewHolder(LayoutInflater.from(parent.context), parent)
            else -> {
                PostsViewHolder(LayoutInflater.from(parent.context), parent, onItemClicked)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ListAdapterModel.Post -> (holder as PostsViewHolder).bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ListAdapterModel.Post -> TYPE_POST
            ListAdapterModel.Loading -> TYPE_LOADING
        }
    }

    object Differ : DiffUtil.ItemCallback<ListAdapterModel>() {
        override fun areItemsTheSame(
            oldItem: ListAdapterModel,
            newItem: ListAdapterModel
        ): Boolean {
            if (newItem is ListAdapterModel.Post && oldItem is ListAdapterModel.Post) {
                return newItem.name == oldItem.name
            }
            return newItem == oldItem
        }

        override fun areContentsTheSame(
            oldItem: ListAdapterModel,
            newItem: ListAdapterModel
        ): Boolean = oldItem == newItem
    }

    companion object {
        private const val TYPE_POST = 1
        private const val TYPE_LOADING = 2
    }
}
