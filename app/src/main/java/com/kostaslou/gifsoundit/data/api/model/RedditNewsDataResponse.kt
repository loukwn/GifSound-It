package com.kostaslou.gifsoundit.data.api.model

class RedditNewsDataResponse(
    val title: String,
    val permalink: String,
    val score: Int,
    val is_self: Boolean,
    val created_utc: Long,
    val thumbnail: String,
    val url: String
)
