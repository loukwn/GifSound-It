package com.loukwn.gifsoundit.postdata

import com.loukwn.gifsoundit.domain.DataState
import com.loukwn.gifsoundit.postdata.model.domain.PostResponse
import io.reactivex.Observable

interface PostRepository {
    val postDataObservable: Observable<DataState<PostResponse>>

    fun getPosts(
        sourceType: SourceTypeDTO,
        filterType: FilterTypeDTO,
        after: String
    )

    fun refreshAuthTokenIfNeeded()
    fun clear()
}

sealed class FilterTypeDTO {
    object Hot : FilterTypeDTO()
    object New : FilterTypeDTO()
    data class Top(val type: TopFilterTypeDTO) : FilterTypeDTO()
}

enum class TopFilterTypeDTO(val apiLabel: String) {
    Hour(apiLabel = "hour"),
    Day(apiLabel = "day"),
    Week(apiLabel = "week"),
    Month(apiLabel = "month"),
    Year(apiLabel = "year"),
    All(apiLabel = "all"),
}

enum class SourceTypeDTO(val apiLabel: String) {
    GifSound(apiLabel = "Gifsound"),
    AnimeGifSound(apiLabel = "AnimeGifSound"),
    MusicGifStation(apiLabel = "MusicGifStation"),
}
