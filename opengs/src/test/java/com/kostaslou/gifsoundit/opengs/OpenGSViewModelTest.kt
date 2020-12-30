package com.kostaslou.gifsoundit.opengs

import com.kostaslou.gifsoundit.opengs.controller.GifSource
import com.kostaslou.gifsoundit.opengs.mappers.QueryToUIModelMapper
import org.junit.Test
import kotlin.test.assertEquals

class OpenGSViewModelTest {

    @Test
    fun `when a gifsound with no second offset is opened`() {
        // given that the gifsound is opened...
        val query = "?mp4=https%3A%2F%2Fi.imgur.com%2F1rWYUyN&v=4sCXkpZsBRg"
        val uiModel = QueryToUIModelMapper()
            .getUIModel(query)

        // ...assert that seconds (offset) are set accordingly
        assertEquals(0, uiModel.secondsVideoDefaultOffset)
    }

    @Test
    fun `when an imgur mp4 gifsound is opened`() {

        // given that the gifsound is opened...
        val query = "?mp4=https%3A%2F%2Fi.imgur.com%2F1rWYUyN&v=4sCXkpZsBRg&s=175"
        val uiModel = QueryToUIModelMapper()
            .getUIModel(query)

        // ...assert that the gif, sound and seconds (offset) are set accordingly
        assertEquals(175, uiModel.secondsVideoDefaultOffset)
        assertEquals("4sCXkpZsBRg", uiModel.soundSource.soundPart)
        assertEquals(GifSource("https://i.imgur.com/1rWYUyN.mp4", GifSource.GifType.MP4), uiModel.gifSource)
    }

    @Test
    fun `when a giphy gif gifsound is opened`() {

        // given that the gifsound is opened...
        val query = "?gif=https%3A%2F%2Fmedia.giphy.com%2Fmedia%2F3o6gDWzmAzrpi5DQU8%2Fgiphy.gif&v=zoP4h3Hv4Uk&s=38"
        val uiModel = QueryToUIModelMapper()
            .getUIModel(query)

        // ...assert that the gif, sound and seconds (offset) are set accordingly
        assertEquals(38, uiModel.secondsVideoDefaultOffset)
        assertEquals("zoP4h3Hv4Uk", uiModel.soundSource.soundPart)
        assertEquals(GifSource("https://media.giphy.com/media/3o6gDWzmAzrpi5DQU8/giphy.gif", GifSource.GifType.GIF), uiModel.gifSource)
    }

    @Test
    fun `when a gfycat webm gifsound is opened`() {

        // given that the gifsound is opened...
        val query = "?webm=https%3A%2F%2Fgiant.gfycat.com%2FFarflungFewIndri&v=zHalXjs0cDA&s=286"
        val uiModel = QueryToUIModelMapper()
            .getUIModel(query)

        // ...assert that the gif, sound and seconds (offset) are set accordingly
        assertEquals(286, uiModel.secondsVideoDefaultOffset)
        assertEquals("zHalXjs0cDA", uiModel.soundSource.soundPart)
        assertEquals(GifSource("https://giant.gfycat.com/FarflungFewIndri.webm", GifSource.GifType.MP4), uiModel.gifSource)
    }

    @Test
    fun `when an imgur gifv gifsound is opened`() {

        // given that the gifsound is opened...
        val query = "?gifv=ZefKQD3&v=VYPsoxpt0BU&s=9"
        val uiModel = QueryToUIModelMapper()
            .getUIModel(query)

        // ...assert that the gif, sound and seconds (offset) are set accordingly
        assertEquals(9, uiModel.secondsVideoDefaultOffset)
        assertEquals("VYPsoxpt0BU", uiModel.soundSource.soundPart)
        assertEquals(GifSource("https://i.imgur.com/ZefKQD3.mp4", GifSource.GifType.MP4), uiModel.gifSource)
    }
}
