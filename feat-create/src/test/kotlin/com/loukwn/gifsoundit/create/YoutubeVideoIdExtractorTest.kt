package com.loukwn.gifsoundit.create

import org.junit.Assert.assertEquals
import org.junit.Test

internal class YoutubeVideoIdExtractorTest {
    @Test
    fun `youtu_be with arguments`() {
        val link = "youtu.be/id123?utm_campaign=yolo"
        val id = YoutubeVideoIdExtractor.getId(link)

        assertEquals("id123", id)
    }

    @Test
    fun `youtu_be no arguments`() {
        val link = "youtu.be/id123"
        val id = YoutubeVideoIdExtractor.getId(link)

        assertEquals("id123", id)
    }

    @Test
    fun `youtube_me with arguments`() {
        val link = "youtube.me/id123?utm_campaign=yolo"
        val id = YoutubeVideoIdExtractor.getId(link)

        assertEquals("id123", id)
    }

    @Test
    fun `youtube_me no arguments`() {
        val link = "youtube.me/id123"
        val id = YoutubeVideoIdExtractor.getId(link)

        assertEquals("id123", id)
    }

    @Test
    fun `youtube_com with arguments`() {
        val link = "youtube.com?v=id123&utm_campaign=yolo"
        val id = YoutubeVideoIdExtractor.getId(link)

        assertEquals("id123", id)
    }

    @Test
    fun `youtube_com no arguments`() {
        val link = "youtube.com?v=id123"
        val id = YoutubeVideoIdExtractor.getId(link)

        assertEquals("id123", id)
    }
}
