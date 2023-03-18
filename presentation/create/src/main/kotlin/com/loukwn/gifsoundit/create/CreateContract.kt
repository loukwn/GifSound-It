package com.loukwn.gifsoundit.create

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

internal interface CreateContract {

    interface Listener {
        fun onBackButtonClicked()
        fun onSoundSelected(link: String, secondOffset: Int?)
        fun onGifSelected(link: String)
        fun onDismissSoundClicked()
        fun onDismissGifClicked()
        fun onCreateClicked()
    }

    interface ViewModel: Listener {
        val uiModelFlow: Flow<UiModel>
        val events: Channel<Event>
    }

    data class UiModel(
        val gifSelected: String? = null,
        val soundModel: SoundModel = SoundModel(),
    )

    data class SoundModel(
        val selectedLink: String? = null,
        val soundPreviewModel: SoundPreviewModel = SoundPreviewModel.NoData(""),
    )

    @Immutable
    sealed class SoundPreviewModel {
        data class Data(val soundPreviewState: SoundPreviewState): SoundPreviewModel()
        data class NoData(val text: String): SoundPreviewModel()
    }

    data class SoundPreviewState(
        val imageUrl: String,
        val title: String,
    )

    sealed class Event {
        object Close: Event()
        data class OpenGs(val url: String): Event()
    }
}
