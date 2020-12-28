package com.kostaslou.gifsoundit.list.view.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kostaslou.gifsoundit.list.R
import com.kostaslou.gifsoundit.list.databinding.ItemPostBinding
import com.loukwn.postdata.model.domain.PostModel
import com.kostaslou.gifsoundit.list.util.inflate
import com.kostaslou.gifsoundit.list.util.loadImg
import kotlinx.android.synthetic.main.item_post.view.*
import java.text.SimpleDateFormat
import java.util.*

class PostsViewHolder(
    private val binding: ItemPostBinding,
    private val onItemClicked: (item: ListAdapterModel.Post) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    constructor(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        onItemClicked: (item: ListAdapterModel.Post) -> Unit,
    ) : this(ItemPostBinding.inflate(inflater, parent, false), onItemClicked)

    fun bind(item: ListAdapterModel.Post) = with(binding) {
        // click listeners
        root.setOnClickListener { onItemClicked(item) }
        linkButton.setOnClickListener {
            it.context?.let { ctx ->
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.permalink))
                ctx.startActivity(browserIntent)
            }
        }

        // load image
        val imageUrl = if (item.isSelf) {
            ""
        } else {
            item.thumbnailUrl
        }
        postThumb.loadImg(imageUrl, R.drawable.placeholder)

        // title
        postTitle.text = item.title

        // score
        postScore.text = item.score.toString()

        // date
        postDate.text = item.dateString
    }
}
