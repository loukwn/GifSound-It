package com.kostaslou.gifsoundit.data.api

// the access token
class RedditTokenResponse(
        val access_token: String,
        val expires_in: String
)

// the response model broken down in parts
class RedditPostResponse(val data: RedditDataResponse)

class RedditDataResponse(
        val children: List<RedditPostChildrenResponse>,
        val after: String?,
        val before: String?
)

class RedditPostChildrenResponse(val data: RedditNewsDataResponse)

class RedditNewsDataResponse(
        val title: String,
        val permalink: String,
        val score: Int,
        val is_self: Boolean,
        val created_utc: Long,
        val thumbnail: String,
        val url: String
)
