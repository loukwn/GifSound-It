package com.kostaslou.gifsoundit.ui.home

import android.os.Parcelable
import com.kostaslou.gifsoundit.ui.home.adapter.ViewType
import com.kostaslou.gifsoundit.util.commons.AdapterConstants
import kotlinx.android.parcel.Parcelize

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
