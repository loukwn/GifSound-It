package com.kostaslou.gifsoundit.list.util

import com.kostaslou.gifsoundit.list.FilterType
import com.kostaslou.gifsoundit.list.SourceType
import com.kostaslou.gifsoundit.postdata.FilterTypeDTO
import com.kostaslou.gifsoundit.postdata.SourceTypeDTO
import com.kostaslou.gifsoundit.postdata.TopFilterTypeDTO

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
