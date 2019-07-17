package com.kostaslou.gifsoundit.adapter

import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.kostaslou.gifsoundit.commons.AdapterConstants
import com.kostaslou.gifsoundit.commons.PostModel
import com.kostaslou.gifsoundit.commons.RedditConstants


// the main adapter for a fragment

class MainPostAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    private var items: ArrayList<ViewType>  // list of items
    private var delegateAdapters = androidx.collection.SparseArrayCompat<ViewTypeDelegateAdapter>()   // the different adapters as a map with their viewtype as key

    // loading item is just an object that implements the viewtype interface
    private val loadingItem = object : ViewType {
        override fun getViewType() = AdapterConstants.LOADING
    }

    init {
        // we add the different adapters
        delegateAdapters.put(AdapterConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(AdapterConstants.POSTS, PostDelegateAdapter())

        // init arraylist and we add the loading item
        items = ArrayList()
        items.add(loadingItem)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // todo: TA THAYMASTIKA
    // based on which viewtype we have we select adapter and we perform the respective onCreateViewHolder and onBindViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return delegateAdapters.get(viewType)!!.onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        delegateAdapters.get(getItemViewType(position))!!.onBindViewHolder(holder, this.items[position])
    }

    // we get the viewtype
    override fun getItemViewType(position: Int): Int {
        return this.items[position].getViewType()
    }

    // functions to interact with fragment
    fun addPosts(posts: List<PostModel>) {

        // add new posts
        val initPosition = items.lastIndex
        items.addAll(initPosition, posts)
        notifyDataSetChanged()
    }

    fun clearAndAddPosts(posts: List<PostModel>) {
        items.clear()
        items.addAll(posts)
        if (posts.size == RedditConstants.NUM_OF_POSTS_PER_REQUEST)
            items.add(loadingItem)
        notifyDataSetChanged()
    }

    // it loops the items list and returns (casted) the ones that are posts
    fun getPosts(): List<PostModel> =
            items
                    .filter { it.getViewType() == AdapterConstants.POSTS }
                    .map { it as PostModel }

}