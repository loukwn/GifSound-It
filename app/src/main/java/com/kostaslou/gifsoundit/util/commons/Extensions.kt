@file:JvmName("ExtensionsUtils")

package com.kostaslou.gifsoundit.util.commons

import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.kostaslou.gifsoundit.util.GlideApp
import com.kostaslou.gifsoundit.util.RxSchedulers
import io.reactivex.Single

// extend the properties(functions) of some of our classes, so that our code is cleaner! God Bless! :3

fun <T> Single<T>.schedulerSetup(rxSchedulers: RxSchedulers):
        Single<T> {
    return this.subscribeOn(rxSchedulers.ioScheduler)
            .observeOn(rxSchedulers.androidScheduler)
}

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun ImageView.loadImg(imageUrl: String, defDrawable: Int) {
    if (TextUtils.isEmpty(imageUrl) || !Patterns.WEB_URL.matcher(imageUrl).matches()) {
        GlideApp.with(context)
                .load(defDrawable)
                .into(this)
    } else {
        GlideApp.with(context)
                .load(imageUrl)
                .into(this)
    }
}