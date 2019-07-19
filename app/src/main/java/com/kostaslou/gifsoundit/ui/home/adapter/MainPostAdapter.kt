package com.kostaslou.gifsoundit.ui.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kostaslou.gifsoundit.util.commons.AdapterConstants
import com.kostaslou.gifsoundit.util.commons.PostModel
import com.kostaslou.gifsoundit.util.commons.RedditConstants


// the main adapter for a fragment

class MainPostAdapter(itemListener: (PostModel) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: ArrayList<ViewType>  // list of items
    private var delegateAdapters = androidx.collection.SparseArrayCompat<ViewTypeDelegateAdapter>()   // the different adapters as a map with their viewtype as key

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

    // functions to interact with fragment
//    fun addPosts(posts: List<PostModel>) {
//
//        // add new posts
//        val initPosition = items.lastIndex
//        items.addAll(initPosition, posts)
//        notifyDataSetChanged()
//    }

    fun clearAndAddPosts(posts: List<PostModel>) {
        items.clear()
        items.addAll(posts)
        if (posts.size == RedditConstants.NUM_OF_POSTS_PER_REQUEST)
            items.add(loadingItem)

        // TODO: CHANGE TO DIFFUTIL instead of notify
//        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(MyDiffCallback(this.items))
//        diffResult.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

//    fun clearPosts() {
//        items.clear()
//        items.add(loadingItem)
//        notifyDataSetChanged()
//    }

    // it loops the items list and returns (casted) the ones that are posts
//    fun getPosts(): List<PostModel> =
//            items
//                    .filter { it.getViewType() == AdapterConstants.POSTS }
//                    .map { it as PostModel }

}