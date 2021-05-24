package com.kostaslou.gifsoundit.postdata.model.api

import androidx.annotation.Keep

@Keep
internal class RedditSubredditDataResponse(
    val children: List<RedditPostChild>,
    val after: String?
)
