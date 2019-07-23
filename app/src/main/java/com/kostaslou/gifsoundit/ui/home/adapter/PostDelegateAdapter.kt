package com.kostaslou.gifsoundit.ui.home.adapter

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kostaslou.gifsoundit.R
import com.kostaslou.gifsoundit.ui.home.model.PostModel
import com.kostaslou.gifsoundit.util.commons.inflate
import com.kostaslou.gifsoundit.util.commons.loadImg
import kotlinx.android.synthetic.main.item_post.view.*
import java.text.SimpleDateFormat
import java.util.*

// the adapter for the actual posts
class PostDelegateAdapter(val itemListener: (PostModel) -> Unit) : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return PostsViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as PostsViewHolder
        holder.bind(item as PostModel)
        holder.itemView.setOnClickListener { itemListener(item) }
    }

    class PostsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_post)), View.OnClickListener  {

        private var postLink : String? = null
        private var postPerma : String? = null
        private var isSelf = false

        init {
            itemView.linkButton.setOnClickListener(this)
        }

        fun bind(item: PostModel) = with(itemView) {
            // is self, perma, post link
            isSelf = item.isSelf
            postLink = item.url
            postPerma = item.permalink

            // load image
            val imageUrl = if (isSelf) {
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
            val date = Date(item.created * 1000)
            val format = SimpleDateFormat("EEE, d MMM yyyy, HH:mm", Locale.UK)
            postDate.text = format.format(date)
        }

        override fun onClick(v: View?) {
            val ctx = v?.context ?: return

            if (v == itemView.linkButton || isSelf) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(postPerma))
                ctx.startActivity(browserIntent)
            }
        }
    }
}