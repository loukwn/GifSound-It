package com.loukwn.gifsoundit.create

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.loukwn.gifsoundit.common.ui.PreviewContainer
import com.loukwn.gifsoundit.common.ui.theme.GifSoundItTheme
import com.loukwn.gifsoundit.common.ui.theme.gsColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.loukwn.gifsoundit.common.util.DataState

@ExperimentalAnimationApi
@Composable
internal fun CreateView(uiModel: UiModel, listener: CreateContract.Listener) {
    GifSoundItTheme {
        Scaffold(
            topBar = @Composable { CreateToolbar() },
        ) {
            LazyColumn {
                item { SoundSection(uiModel, listener) }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
internal fun SoundSection(uiModel: UiModel, listener: CreateContract.Listener) {
    GsCard(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(16.dp)
    ) {
        Text("Sound", style = MaterialTheme.typography.h2)
        val corners: Dp by animateDpAsState(if (uiModel.soundPreview.visible) 0.dp else 16.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .height(IntrinsicSize.Max)
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var textValue by remember { mutableStateOf("") }

            TextField(
                value = textValue,
                onValueChange = { textValue = it },
                label = { Text("YouTube Link") },
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    bottomStart = corners,
                ),
                colors = TextFieldDefaults.textFieldColors(
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { listener.onGoPressed() },
                shape = RoundedCornerShape(
                    topEnd = 16.dp,
                    bottomEnd = corners,
                ),
                modifier = Modifier.fillMaxHeight(1f)
            ) {
                Text("Go", maxLines = 1)
            }
        }
        AnimatedVisibility(visible = true) {
            Box(modifier = Modifier.fillMaxWidth(1f), contentAlignment = Alignment.Center) {
//                if (uiModel.youtubePreviewState is DataState.Loading) {
                    Text(
                        modifier = Modifier.padding(top = 20.dp, bottom = 4.dp),
                        text = "Loading preview...",
                        style = MaterialTheme.typography.h2
                    )
//                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(MaterialTheme.colors.background.copy(alpha = 0.5f))
                    .padding(16.dp)
            ) {
                IconButton(onClick = {}, modifier = Modifier.align(Alignment.End)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close_24),
                        contentDescription = null
                    )
                }
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Yellow)
                )
            }
        }
    }
}

@Composable
internal fun GsCard(
    modifier: Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        backgroundColor = gsColors.material.surface,
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
internal fun CreateToolbar() {
    TopAppBar(
        backgroundColor = gsColors.primary,
    ) {
        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back_24),
                contentDescription = null
            )
        }
        Text(
            text = stringResource(id = R.string.create_title),
            style = MaterialTheme.typography.h1.merge(TextStyle(color = MaterialTheme.colors.onPrimary)),
            modifier = Modifier
                .padding(start = 4.dp)
                .fillMaxWidth(1f)
        )
    }
}

@ExperimentalAnimationApi
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun CreateViewPreviewDark() {

    var uiModel by remember { mutableStateOf(UiModel.default()) }
    val previewView = PreviewView {
        uiModel = it
    }

    PreviewContainer {
        CreateView(uiModel = uiModel, listener = previewView.listener)
    }
}

@ExperimentalAnimationApi
@Preview
@Composable
internal fun CreateViewPreviewLight() {

    var uiModel by remember { mutableStateOf(UiModel.default()) }
    val previewView = PreviewView {
        uiModel = it
    }

    PreviewContainer {
        CreateView(uiModel = uiModel, listener = previewView.listener)
    }
}

private class PreviewView(
    private val doOnNewUiModel: (UiModel) -> Unit
) {
    private var uiModel = UiModel.default()

    val listener = object : CreateContract.Listener {
        override fun onGoPressed() {
//            uiModel = uiModel.copy(youtubeSelected = !uiModel.youtubeSelected)
            doOnNewUiModel(uiModel)
        }
    }
}