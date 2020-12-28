package com.kostaslou.gifsoundit.common.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.Toast

fun Context.toast(message: CharSequence) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.selector(title: String, options: Array<String>, onSelected: (Int) -> Unit) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setItems(options) { _, which -> onSelected(which) }
        .show()
}
