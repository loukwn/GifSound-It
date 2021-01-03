package com.kostaslou.gifsoundit.list.view.adapter

import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.kostaslou.gifsoundit.list.R
import com.kostaslou.gifsoundit.list.databinding.ItemPostBinding

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
        postThumb.loadImg(imageUrl, R.drawable.ic_placeholder_24)


        // title
        postTitle.text = item.title

        // score
        postScore.text = item.score.toString()

        // date
        postDate.text = item.dateString
    }
}

fun ImageView.loadImg(imageUrl: String, defDrawable: Int) {
    if (TextUtils.isEmpty(imageUrl) || !Patterns.WEB_URL.matcher(imageUrl).matches()) {
        setImageDrawable(ContextCompat.getDrawable(context, defDrawable))
    } else {
        Glide.with(context)
            .load(imageUrl)
            .into(this)
    }
}
