package com.loukwn.postdata.model.api

class RedditPostChildData(
    val name: String,
    val title: String,
    val permalink: String,
    val score: Int,
    val is_self: Boolean,
    val created_utc: Long,
    val thumbnail: String,
    val url: String
)
