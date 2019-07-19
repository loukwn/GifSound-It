package com.kostaslou.gifsoundit.ui.home.adapter

import android.graphics.Color
import android.view.ViewGroup
import com.kostaslou.gifsoundit.R
import com.kostaslou.gifsoundit.commons.inflate
import kotlinx.android.synthetic.main.item_loading.view.*


// the adapter for the loading item
class LoadingDelegateAdapter : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup) = TurnsViewHolder(parent)
    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, item: ViewType) {}

    class TurnsViewHolder(parent: ViewGroup) : androidx.recyclerview.widget.RecyclerView.ViewHolder(parent.inflate(R.layout.item_loading)) {

        // we change the color of the progressbar to black
        init {
            itemView.progress.indeterminateDrawable.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }
}