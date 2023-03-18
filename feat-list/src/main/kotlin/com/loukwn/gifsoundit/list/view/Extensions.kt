package com.loukwn.gifsoundit.list.view

import android.content.Context
import android.widget.ProgressBar
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat

fun ProgressBar.tintWithColorRes(context: Context, @ColorRes colorRes: Int) {
    this.indeterminateDrawable.colorFilter =
        BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            ContextCompat.getColor(context, colorRes),
            BlendModeCompat.SRC_ATOP
        )
}
