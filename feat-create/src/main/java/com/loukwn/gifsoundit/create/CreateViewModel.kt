package com.loukwn.gifsoundit.create

import androidx.lifecycle.ViewModel
import com.loukwn.gifsoundit.common.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor() : ViewModel() {
    private val _uiModelFlow = MutableStateFlow(UiModel.default())
    val uiModelFlow: StateFlow<UiModel> = _uiModelFlow

    fun goPressed() {
//        val newVisibility = !_uiModelFlow.value.youtubeSelected
//        _uiModelFlow.value = _uiModelFlow.value.copy(youtubeSelected = newVisibility)
    }
}

data class UiModel(
    val soundPreview: SoundPreviewModel,
) {
    companion object {
        fun default(): UiModel = UiModel(
            soundPreview = SoundPreviewModel.default(),
        )
    }
}


data class SoundPreviewModel(
    val visible: Boolean,
    val state: DataState<SoundPreviewState>?
) {
    companion object {
        fun default(): SoundPreviewModel = SoundPreviewModel(
            visible = false,
            state = null,
        )
    }
}

data class SoundPreviewState(
    val imageUrl: String,
    val title: String,
    val subtitle: String,
)