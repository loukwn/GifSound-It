package com.loukwn.gifsoundit.list.util

import com.loukwn.gifsoundit.list.FilterType
import com.loukwn.gifsoundit.list.SourceType
import com.loukwn.gifsoundit.postdata.FilterTypeDTO
import com.loukwn.gifsoundit.postdata.SourceTypeDTO
import com.loukwn.gifsoundit.postdata.TopFilterTypeDTO

internal fun FilterType.toDTO(): FilterTypeDTO {
    return when (this) {
        FilterType.Hot -> FilterTypeDTO.Hot
        FilterType.New -> FilterTypeDTO.New
        FilterType.TopHour -> FilterTypeDTO.Top(type = TopFilterTypeDTO.Hour)
        FilterType.TopDay -> FilterTypeDTO.Top(type = TopFilterTypeDTO.Day)
        FilterType.TopWeek -> FilterTypeDTO.Top(type = TopFilterTypeDTO.Week)
        FilterType.TopMonth -> FilterTypeDTO.Top(type = TopFilterTypeDTO.Month)
        FilterType.TopYear -> FilterTypeDTO.Top(type = TopFilterTypeDTO.Year)
        FilterType.TopAll -> FilterTypeDTO.Top(type = TopFilterTypeDTO.All)
    }
}

internal fun SourceType.toDTO(): SourceTypeDTO {
    return when (this) {
        SourceType.GifSound -> SourceTypeDTO.GifSound
        SourceType.AnimeGifSound -> SourceTypeDTO.AnimeGifSound
        SourceType.MusicGifStation -> SourceTypeDTO.MusicGifStation
    }
}
