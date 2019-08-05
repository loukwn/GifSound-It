package com.kostaslou.gifsoundit.home.data.api.model

class RedditDataResponse(
    val children: List<RedditPostChildrenResponse>,
    val after: String?,
    val before: String?
)
