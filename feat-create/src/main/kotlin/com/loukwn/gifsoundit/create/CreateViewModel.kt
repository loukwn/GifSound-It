package com.loukwn.gifsoundit.create

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import javax.inject.Inject

@HiltViewModel
internal class CreateViewModel @Inject constructor(
    private val resources: Resources,
    private val linkMapper: GifSoundPartToLinkMapper,
) : ViewModel(), CreateContract.ViewModel {
    override val uiModelFlow = MutableStateFlow(CreateContract.UiModel())
    override val events = Channel<CreateContract.Event>(capacity = Channel.UNLIMITED)

    private var gifLink: String? = null
    private var soundLink: String? = null
    private var soundSecondOffset: Int? = null

    private var fetchPreviewJob: Job? = null

    override fun onBackButtonClicked() {
        events.trySend(CreateContract.Event.Close)
    }

    override fun onGifSelected(link: String) {
        val trimmedLink = link.trim().ifEmpty { null }
        if (trimmedLink != gifLink) {
            gifLink = link
            uiModelFlow.tryEmit(
                uiModelFlow.value.copy(gifSelected = trimmedLink)
            )
        }
    }

    override fun onDismissGifClicked() {
        gifLink = null
        uiModelFlow.tryEmit(
            uiModelFlow.value.copy(gifSelected = null)
        )
    }

    override fun onSoundSelected(link: String, secondOffset: Int?) {
        val trimmedLink = link.trim().ifEmpty { null }
        if (!trimmedLink.isNullOrEmpty()) {
            soundSecondOffset = secondOffset

            if (trimmedLink != soundLink) {
                soundLink = link
                uiModelFlow.tryEmit(
                    uiModelFlow.value.copy(
                        soundModel = CreateContract.SoundModel(
                            selectedLink = YoutubeVideoIdExtractor.getId(trimmedLink),
                            soundPreviewModel = CreateContract.SoundPreviewModel.NoData(
                                text = resources.getString(R.string.create_sound_preview_loading)
                            )
                        )
                    )
                )

                fetchSoundLinkPreview(trimmedLink)
            }
        } else {
            clearSound()
        }
    }

    override fun onDismissSoundClicked() {
        clearSound()
    }

    private fun clearSound() {
        soundLink = null
        soundSecondOffset = null
        uiModelFlow.tryEmit(
            uiModelFlow.value.copy(soundModel = CreateContract.SoundModel())
        )
    }

    private fun fetchSoundLinkPreview(link: String) {
        fetchPreviewJob?.cancel()
        fetchPreviewJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val id = YoutubeVideoIdExtractor.getId(link)

                val imageUrl = "https://img.youtube.com/vi/$id/mqdefault.jpg"

                val document = Jsoup.connect(link).get()
                val metaTags = document.getElementsByTag("meta")
                val title = metaTags
                    .firstOrNull { it.attr("name") == "title" }
                    ?.attr("content")
                    ?: ""

                val currentValue = uiModelFlow.value
                uiModelFlow.emit(
                    currentValue.copy(
                        soundModel = currentValue.soundModel.copy(
                            soundPreviewModel = CreateContract.SoundPreviewModel.Data(
                                CreateContract.SoundPreviewState(
                                    imageUrl, title
                                )
                            )
                        )
                    )
                )
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is UndeterminedVideoIdException -> {
                        "Could not determine video id..."
                    }
                    else -> "An error occured"
                }

                val currentValue = uiModelFlow.value
                uiModelFlow.emit(
                    currentValue.copy(
                        soundModel = currentValue.soundModel.copy(
                            soundPreviewModel = CreateContract.SoundPreviewModel.NoData(
                                errorMessage
                            )
                        )
                    )
                )
            }
        }
    }

    override fun onCreateClicked() {
        val gif = gifLink ?: return
        val sound = soundLink ?: return
        val seconds = soundSecondOffset ?: 0
        val finalUrl = linkMapper.map(gif, sound, seconds)

        events.trySend(CreateContract.Event.OpenGs(finalUrl))
    }
}

class UndeterminedVideoIdException(val id: String) : Exception()

internal object YoutubeVideoIdExtractor {
    @Throws(UndeterminedVideoIdException::class)
    fun getId(link: String): String {
        return try {
            when {
                link.contains("youtu.be/") -> {
                    link.split("youtu.be/")[1].split("?")[0]
                }
                link.contains("youtube.me/") -> {
                    link.split("youtube.me/")[1].split("?")[0]
                }
                link.contains("youtube.com") -> {
                    link.split("?v=")[1].split("&")[0]
                }
                else -> ""
            }
        } catch (e: Exception) {
            throw UndeterminedVideoIdException(link)
        }
    }
}
