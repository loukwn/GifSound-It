package com.kostaslou.gifsoundit.ui.home.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// contains the list of PostModels
@Parcelize
data class LocalPostData (
        var list: List<PostModel>,
        var before: String,
        var after: String
) : Parcelable
