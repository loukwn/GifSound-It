package com.loukwn.gifsoundit.create

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.loukwn.gifsoundit.common.ui.PreviewContainer
import com.loukwn.gifsoundit.common.ui.theme.GifSoundItTheme
import com.loukwn.gifsoundit.common.ui.theme.SquareShape
import com.loukwn.gifsoundit.common.ui.theme.gsColors

@ExperimentalAnimationApi
@Composable
internal fun CreateView(uiModel: CreateContract.UiModel, listener: CreateContract.Listener) {
    GifSoundItTheme {
        Scaffold(
            topBar = @Composable { CreateToolbar(listener::onBackButtonClicked) },
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                GifSection(
                    gifSelected = uiModel.gifSelected,
                    onGifSelected = listener::onGifSelected,
                    onDismissGifClicked = listener::onDismissGifClicked
                )
                SoundSection(
                    soundModel = uiModel.soundModel,
                    onSoundSelected = listener::onSoundSelected,
                    onDismissSoundClicked = listener::onDismissSoundClicked
                )
                Button(
                    onClick = listener::onCreateClicked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(id = R.string.create_button),
                        modifier = Modifier.padding(vertical = 8.dp),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun GifSection(
    gifSelected: String?,
    onGifSelected: (String) -> Unit,
    onDismissGifClicked: () -> Unit,
) {
    val corners: Dp by animateDpAsState(if (gifSelected != null) 0.dp else 16.dp)
    GsCard(modifier = Modifier.fillMaxWidth(1f)) {
        Text(stringResource(id = R.string.create_gif_title), style = MaterialTheme.typography.h2)
        Row(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = corners,
                        bottomEnd = corners,
                    )
                )
                .height(IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var gifTextValue by remember { mutableStateOf("") }

            TextField(
                value = gifTextValue,
                onValueChange = { gifTextValue = it },
                label = { Text(stringResource(id = R.string.create_gif_placeholder)) },
                shape = SquareShape,
                colors = TextFieldDefaults.textFieldColors(
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { onGifSelected(gifTextValue) },
                shape = SquareShape,
                modifier = Modifier.fillMaxHeight(1f)
            ) {
                Text(stringResource(id = R.string.common_go), maxLines = 1)
            }
        }
        AnimatedVisibility(visible = gifSelected != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        )
                    )
                    .background(MaterialTheme.colors.background.copy(alpha = 0.5f))
                    .padding(vertical = 16.dp)
                    .heightIn(min = 56.dp),
            ) {
                if (gifSelected != null) {
                    Text(
                        text = stringResource(id = R.string.create_gif_selected, gifSelected),
                        style = MaterialTheme.typography.h2,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    )
                    IconButton(
                        onClick = onDismissGifClicked,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close_24),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun SoundSection(
    soundModel: CreateContract.SoundModel,
    onSoundSelected: (String, Int?) -> Unit,
    onDismissSoundClicked: () -> Unit,
) {
    val previewModel = soundModel.soundPreviewModel

    GsCard(modifier = Modifier.fillMaxWidth(1f)) {
        Text(stringResource(id = R.string.create_sound_title), style = MaterialTheme.typography.h2)
        val corners: Dp by animateDpAsState(if (soundModel.selectedLink != null) 0.dp else 16.dp)

        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        ) {
            var youtubeLinkTextValue by remember { mutableStateOf("") }
            var secTextValue by remember { mutableStateOf("") }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = corners,
                            bottomEnd = corners,
                        )
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    TextField(
                        value = youtubeLinkTextValue,
                        onValueChange = { youtubeLinkTextValue = it },
                        label = { Text(stringResource(id = R.string.create_sound_placeholder)) },
                        shape = SquareShape,
                        colors = TextFieldDefaults.textFieldColors(
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = stringResource(id = R.string.create_sound_offset),
                            style = MaterialTheme.typography.h2,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        TextField(
                            value = secTextValue,
                            onValueChange = { secTextValue = it },
                            label = { Text(stringResource(id = R.string.create_sound_offset_placeholder)) },
                            shape = SquareShape,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            colors = TextFieldDefaults.textFieldColors(
                                disabledIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            modifier = Modifier.width(100.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        val seconds = secTextValue.toIntOrNull()
                        onSoundSelected(youtubeLinkTextValue, seconds)
                    },
                    shape = SquareShape,
                    modifier = Modifier.fillMaxHeight(1f)
                ) {
                    Text(stringResource(id = R.string.common_go), maxLines = 1)
                }
            }
        }

        AnimatedVisibility(visible = soundModel.selectedLink != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        )
                    )
                    .background(MaterialTheme.colors.background.copy(alpha = 0.5f)),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .heightIn(min = 56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (soundModel.selectedLink != null) {
                        Text(
                            text = stringResource(
                                id = R.string.create_sound_selected,
                                soundModel.selectedLink,
                            ),
                            style = MaterialTheme.typography.h2,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp)
                        )

                        IconButton(
                            onClick = onDismissSoundClicked,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_close_24),
                                contentDescription = null
                            )
                        }
                    }
                }

                when (previewModel) {
                    is CreateContract.SoundPreviewModel.Data -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            AsyncImage(
                                model = previewModel.soundPreviewState.imageUrl,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(60.dp),
                                contentDescription = null
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                text = previewModel.soundPreviewState.title,
                                style = MaterialTheme.typography.h2,
                                maxLines = 2
                            )
                        }
                    }
                    is CreateContract.SoundPreviewModel.NoData -> {
                        Text(
                            modifier = Modifier.padding(vertical = 16.dp),
                            text = previewModel.text,
                            style = MaterialTheme.typography.h2
                        )
                    }
                }
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
internal fun CreateToolbar(onBackPressed: () -> Unit) {
    TopAppBar(
        backgroundColor = gsColors.primary,
    ) {
        IconButton(onClick = onBackPressed) {
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
    PreviewContainer {
        CreateView(uiModel = CreateContract.UiModel(), listener = emptyListener)
    }
}

@ExperimentalAnimationApi
@Preview
@Composable
internal fun CreateViewPreviewLight() {
    PreviewContainer {
        CreateView(uiModel = CreateContract.UiModel(), listener = emptyListener)
    }
}

private val emptyListener = object : CreateContract.Listener {
    override fun onBackButtonClicked() {}
    override fun onSoundSelected(link: String, secondOffset: Int?) {}
    override fun onGifSelected(link: String) {}
    override fun onDismissSoundClicked() {}
    override fun onDismissGifClicked() {}
    override fun onCreateClicked() {}
}
