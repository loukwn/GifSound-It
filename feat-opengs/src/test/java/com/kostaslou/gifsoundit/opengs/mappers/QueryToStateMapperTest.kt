package com.kostaslou.gifsoundit.opengs.mappers

import com.kostaslou.gifsoundit.opengs.GifSource
import com.kostaslou.gifsoundit.opengs.GifType
import org.junit.Assert.assertEquals
import org.junit.Test

class QueryToStateMapperTest {

    @Test
    fun `when a gifsound with no second offset is opened`() {
        // given that the gifsound is opened...
        val query = "https://www.gifsound.com/?mp4=https%3A%2F%2Fi.imgur.com%2F1rWYUyN&v=4sCXkpZsBRg"
        val uiModel = QueryToStateMapper().getState(query = query, isFromDeepLink = false)

        // ...assert that seconds (offset) are set accordingly
        assertEquals(0, uiModel.soundSource.defaultSecondsOffset)
    }

    @Test
    fun `when an imgur mp4 gifsound is opened`() {

        // given that the gifsound is opened...
        val query = "https://www.gifsound.com/?mp4=https%3A%2F%2Fi.imgur.com%2F1rWYUyN&v=4sCXkpZsBRg&s=175"
        val uiModel = QueryToStateMapper().getState(query = query, isFromDeepLink = false)

        // ...assert that the gif, sound and seconds (offset) are set accordingly
        assertEquals(175, uiModel.soundSource.defaultSecondsOffset)
        assertEquals("4sCXkpZsBRg", uiModel.soundSource.soundUrl)
        assertEquals(GifSource("https://i.imgur.com/1rWYUyN.mp4", GifType.MP4), uiModel.gifSource)
    }

    @Test
    fun `when a giphy gif gifsound is opened`() {

        // given that the gifsound is opened...
        val query = "https://www.gifsound.com/?gif=https%3A%2F%2Fmedia.giphy.com%2Fmedia%2F3o6gDWzmAzrpi5DQU8%2Fgiphy.gif&v=zoP4h3Hv4Uk&s=38"
        val uiModel = QueryToStateMapper().getState(query = query, isFromDeepLink = false)

        // ...assert that the gif, sound and seconds (offset) are set accordingly
        assertEquals(38, uiModel.soundSource.defaultSecondsOffset)
        assertEquals("zoP4h3Hv4Uk", uiModel.soundSource.soundUrl)
        assertEquals(GifSource("https://media.giphy.com/media/3o6gDWzmAzrpi5DQU8/giphy.gif", GifType.GIF), uiModel.gifSource)
    }

    @Test
    fun `when a gfycat webm gifsound is opened`() {

        // given that the gifsound is opened...
        val query = "https://www.gifsound.com/?webm=https%3A%2F%2Fgiant.gfycat.com%2FFarflungFewIndri&v=zHalXjs0cDA&s=286"
        val uiModel = QueryToStateMapper().getState(query = query, isFromDeepLink = false)

        // ...assert that the gif, sound and seconds (offset) are set accordingly
        assertEquals(286, uiModel.soundSource.defaultSecondsOffset)
        assertEquals("zHalXjs0cDA", uiModel.soundSource.soundUrl)
        assertEquals(GifSource("https://giant.gfycat.com/FarflungFewIndri.webm", GifType.MP4), uiModel.gifSource)
    }

    @Test
    fun `when an imgur gifv gifsound is opened`() {

        // given that the gifsound is opened...
        val query = "https://www.gifsound.com/?gifv=ZefKQD3&v=VYPsoxpt0BU&s=9"
        val uiModel = QueryToStateMapper().getState(query = query, isFromDeepLink = false)

        // ...assert that the gif, sound and seconds (offset) are set accordingly
        assertEquals(9, uiModel.soundSource.defaultSecondsOffset)
        assertEquals("VYPsoxpt0BU", uiModel.soundSource.soundUrl)
        assertEquals(GifSource("https://i.imgur.com/ZefKQD3.mp4", GifType.MP4), uiModel.gifSource)
    }
}
