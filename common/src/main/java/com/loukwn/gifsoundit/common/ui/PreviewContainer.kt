package com.loukwn.gifsoundit.common.ui

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import com.loukwn.gifsoundit.common.ui.theme.GifSoundItTheme

@Composable
fun PreviewContainer(
    content: @Composable () -> Unit,
) {
    GifSoundItTheme {
        Scaffold {
            content()
        }
    }
}