package com.kostaslou.gifsoundit.list.view.adapter

import com.loukwn.postdata.model.domain.PostModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class ListAdapterModel {
    data class Post(
        val name: String,
        val title: String,
        val thumbnailUrl: String,
        val dateString: String,
        val score: Int,
        val url: String,
        val permalink: String,
        val isSelf: Boolean,
    ) : ListAdapterModel()

    object Loading: ListAdapterModel()
}

fun PostModel.toAdapterModel(): ListAdapterModel.Post {
    val date = Date(created * 1000)
    val format = SimpleDateFormat("EEE, d MMM yyyy, HH:mm", Locale.UK)
    val dateString = format.format(date)

    return ListAdapterModel.Post(
        name = name,
        title = title,
        thumbnailUrl = thumbnailUrl,
        dateString = dateString,
        permalink = permalink,
        score = score,
        url = url,
        isSelf = isSelf
    )
}
