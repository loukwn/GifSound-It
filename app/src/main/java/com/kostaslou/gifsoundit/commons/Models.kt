package com.kostaslou.gifsoundit.commons

import android.os.Parcel
import android.os.Parcelable
import com.kostaslou.gifsoundit.adapter.ViewType

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
data class LocalPostData(
        var list: List<PostModel>,
        var before: String,
        var after: String
) : Parcelable {
    constructor(source: Parcel) : this(
            source.createTypedArrayList(PostModel.CREATOR),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeTypedList(list)
        writeString(before)
        writeString(after)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<LocalPostData> = object : Parcelable.Creator<LocalPostData> {
            override fun createFromParcel(source: Parcel): LocalPostData = LocalPostData(source)
            override fun newArray(size: Int): Array<LocalPostData?> = arrayOfNulls(size)
        }
    }
}

// the actual post and its data
data class PostModel(
        var title: String,
        var thumbnailUrl: String,
        var created: Long,
        var score: Int,
        var url: String,
        var permalink: String,
        var isSelf: Boolean

) : ViewType, Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte())

    override fun getViewType() = AdapterConstants.POSTS
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(thumbnailUrl)
        parcel.writeLong(created)
        parcel.writeInt(score)
        parcel.writeString(url)
        parcel.writeString(permalink)
        parcel.writeByte(if (isSelf) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostModel> {
        override fun createFromParcel(parcel: Parcel): PostModel {
            return PostModel(parcel)
        }

        override fun newArray(size: Int): Array<PostModel?> {
            return arrayOfNulls(size)
        }
    }

}
