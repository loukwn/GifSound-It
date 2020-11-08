package com.kostaslou.gifsoundit.home.ui.adapter

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.ViewGroup
import com.kostaslou.gifsoundit.home.R
import com.kostaslou.gifsoundit.home.util.commons.inflate
import com.kostaslou.gifsoundit.home.util.commons.setFilter
import kotlinx.android.synthetic.main.item_loading.view.*

// the adapter for the loading item
class LoadingDelegateAdapter : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup) =
        TurnsViewHolder(parent)
    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, item: ViewType) {}

    class TurnsViewHolder(parent: ViewGroup) : androidx.recyclerview.widget.RecyclerView.ViewHolder(parent.inflate(R.layout.item_loading)) {

        // we change the color of the progressbar to black
        init {
            itemView.progress.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        }
    }
}
