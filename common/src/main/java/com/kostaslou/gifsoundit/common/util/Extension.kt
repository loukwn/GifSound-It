package com.kostaslou.gifsoundit.common.util

import android.app.AlertDialog
import android.content.Context
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat

fun Context.toast(message: CharSequence) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.selector(title: String, options: Array<String>, onSelected: (Int) -> Unit) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setItems(options) { _, which -> onSelected(which) }
        .show()
}

fun ProgressBar.tintWithColorRes(context: Context, @ColorRes colorRes: Int) {
    this.indeterminateDrawable.colorFilter =
        BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            ContextCompat.getColor(context, colorRes),
            BlendModeCompat.SRC_ATOP
        )
}

