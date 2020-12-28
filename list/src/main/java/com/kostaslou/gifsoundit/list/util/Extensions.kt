package com.kostaslou.gifsoundit.list.util

import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun ImageView.loadImg(imageUrl: String, defDrawable: Int) {
    if (TextUtils.isEmpty(imageUrl) || !Patterns.WEB_URL.matcher(imageUrl).matches()) {
        Glide.with(context)
            .load(defDrawable)
            .into(this)
    } else {
        Glide.with(context)
            .load(imageUrl)
            .into(this)
    }
}
