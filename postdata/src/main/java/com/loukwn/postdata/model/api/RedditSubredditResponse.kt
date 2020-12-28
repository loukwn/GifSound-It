package com.loukwn.postdata.model.api

import com.loukwn.postdata.model.domain.PostModel
import com.loukwn.postdata.model.domain.PostResponse

class RedditSubredditResponse(val data: RedditSubredditDataResponse)

fun RedditSubredditResponse.toDomainData(): PostResponse {
    val postData = data.children.map {
        val item = it.data

        val url =
            if (item.url.startsWith("/r")) "http://www.reddit.com" + item.url else item.url
        val perma =
            if (item.permalink.startsWith("/r")) "http://www.reddit.com" + item.permalink else item.permalink
        val created = item.created_utc
        val score = item.score
        val isSelf = item.is_self

        PostModel(
            item.name,
            item.title,
            item.thumbnail,
            created,
            score,
            url,
            perma,
            isSelf
        )
    }

    return PostResponse(postData = postData, after = data.after
    )
}
