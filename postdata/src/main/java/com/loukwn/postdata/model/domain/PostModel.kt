package com.loukwn.postdata.model.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostModel(
    val name: String,
    val title: String,
    val thumbnailUrl: String,
    val created: Long,
    val score: Int,
    val url: String,
    val permalink: String,
    val isSelf: Boolean,
) : Parcelable

data class PostResponse(
    val postData: List<PostModel>,
    val after: String?,
)
