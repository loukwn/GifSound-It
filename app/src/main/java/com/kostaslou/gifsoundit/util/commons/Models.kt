package com.kostaslou.gifsoundit.util.commons

import android.os.Parcelable
import com.kostaslou.gifsoundit.ui.home.adapter.ViewType
import kotlinx.android.parcel.Parcelize

//======================== REDDIT PART ======================================//
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

//======================== LOCAL PART =======================================//

// contains the list of PostModels
@Parcelize
data class LocalPostData (
        var list: List<PostModel>,
        var before: String,
        var after: String
) : Parcelable

// the actual post and its data
@Parcelize
data class PostModel (
        var title: String,
        var thumbnailUrl: String,
        var created: Long,
        var score: Int,
        var url: String,
        var permalink: String,
        var isSelf: Boolean

) : ViewType, Parcelable {
    override fun getViewType() = AdapterConstants.POSTS
}
