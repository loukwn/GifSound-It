package com.kostaslou.gifsoundit.ui.home.adapter

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kostaslou.gifsoundit.OpenGSActivity
import com.kostaslou.gifsoundit.R
import com.kostaslou.gifsoundit.ui.home.PostModel
import com.kostaslou.gifsoundit.util.commons.inflate
import com.kostaslou.gifsoundit.util.commons.loadImg
import kotlinx.android.synthetic.main.item_post.view.*
import java.text.SimpleDateFormat
import java.util.*

// the adapter for the actual posts
class PostDelegateAdapter : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return PostsViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as PostsViewHolder
        holder.bind(item as PostModel)
    }

    class PostsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_post)), View.OnClickListener  {

        private var postLink : String? = null
        private var postPerma : String? = null
        private var isSelf = false

        init {
            itemView.setOnClickListener(this)
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
            val date = Date(item.created)
            val format = SimpleDateFormat("EEE, d MMM yyyy, HH:mm", Locale.UK)
            postDate.text = format.format(date)
        }

        override fun onClick(v: View?) {
            val ctx = v?.context ?: return

            if (v == itemView.linkButton || isSelf) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(postPerma))
                ctx.startActivity(browserIntent)
            } else {
                val intent = Intent(ctx, OpenGSActivity::class.java)

                // get the query part of the link
                val partsOfLink = postLink?.split("?")
                val query = if (partsOfLink!=null && partsOfLink.size > 1) {
                    var temp = ""
                    for (i in 1 until partsOfLink.size)
                        temp += "?" + partsOfLink[i]
                    temp.substring(1)
                } else {
                    null
                } ?: return

                intent.putExtra("query", query)
                ctx.startActivity(intent)
            }
        }
    }
}