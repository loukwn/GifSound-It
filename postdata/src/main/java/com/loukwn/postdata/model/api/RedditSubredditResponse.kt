package com.loukwn.postdata.model.api

import androidx.annotation.Keep
import com.loukwn.postdata.PostRepositoryImpl.Companion.NUM_OF_POSTS_PER_REQUEST
import com.loukwn.postdata.model.domain.PostModel
import com.loukwn.postdata.model.domain.PostResponse

@Keep
internal class RedditSubredditResponse(val data: RedditSubredditDataResponse)

internal fun RedditSubredditResponse.toDomainData(): PostResponse {
    val canFetchMore = data.children.size >= NUM_OF_POSTS_PER_REQUEST

    val postData = data.children.filterNot { it.data.over_18 }.map {
        val item = it.data

        val url = if (item.url.startsWith("/r")) {
            "http://www.reddit.com${item.url}"
        } else {
            item.url
        }
        val perma = if (item.permalink.startsWith("/r")) {
            "http://www.reddit.com${item.permalink}"
        } else {
            item.permalink
        }

        PostModel(
            name = item.name,
            title = item.title,
            thumbnailUrl = item.thumbnail,
            created = item.created_utc,
            score = item.score,
            url = url,
            permalink = perma,
            isSelf = item.is_self,
        )
    }

    return PostResponse(postData = postData, canFetchMore = canFetchMore, after = data.after)
}
