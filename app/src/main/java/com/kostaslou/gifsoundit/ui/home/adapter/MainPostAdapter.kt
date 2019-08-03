package com.kostaslou.gifsoundit.ui.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kostaslou.gifsoundit.util.commons.AdapterConstants
import com.kostaslou.gifsoundit.ui.home.model.PostModel
import com.kostaslou.gifsoundit.util.commons.RedditConstants

// the main adapter for a fragment

class MainPostAdapter(itemListener: (PostModel) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: ArrayList<ViewType> // list of items
    private var delegateAdapters = androidx.collection.SparseArrayCompat<ViewTypeDelegateAdapter>() // the different adapters as a map with their viewtype as key

    // loading item is just an object that implements the viewtype interface
    private val loadingItem = object : ViewType {
        override fun getViewType() = AdapterConstants.LOADING
    }

    init {
        // we add the different adapters
        delegateAdapters.put(AdapterConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(AdapterConstants.POSTS, PostDelegateAdapter(itemListener))

        // init arraylist and we add the loading item
        items = ArrayList()
        items.add(loadingItem)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // based on which viewtype we have we select adapter and we perform the respective onCreateViewHolder and onBindViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegateAdapters.get(viewType)!!.onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateAdapters.get(getItemViewType(position))!!.onBindViewHolder(holder, this.items[position])
    }

    // we get the viewtype
    override fun getItemViewType(position: Int): Int {
        return this.items[position].getViewType()
    }

    fun clearAndAddPosts(posts: List<PostModel>) {
        items.clear()
        items.addAll(posts)
        if (posts.size == RedditConstants.NUM_OF_POSTS_PER_REQUEST)
            items.add(loadingItem)

        notifyDataSetChanged()
    }
}