package com.loukwn.gifsoundit.create

import org.junit.Test
import org.junit.Assert.assertEquals

internal class GifSoundPartToLinkMapperTest {

    private val mapper = GifSoundPartToLinkMapper()

    @Test
    fun `Imgur links that start with i_imgur_com`() {
        assertEquals(
            "gifv=IMGURTAG",
            mapper.getGifPart("https://i.imgur.com/IMGURTAG")
        )

        assertEquals(
            "gifv=IMGURTAG",
            mapper.getGifPart("https://i.imgur.com/IMGURTAG.mp4")
        )

        assertEquals(
            "gifv=IMGURTAG",
            mapper.getGifPart("https://i.imgur.com/IMGURTAG.gif")
        )
    }

    @Test
    fun `Imgur links that start with imgur_com`() {
        assertEquals(
            "gifv=IMGURTAG",
            mapper.getGifPart("https://imgur.com/IMGURTAG")
        )

        assertEquals(
            "gifv=IMGURTAG",
            mapper.getGifPart("https://imgur.com/IMGURTAG.mp4")
        )

        assertEquals(
            "gifv=IMGURTAG",
            mapper.getGifPart("https://imgur.com/IMGURTAG.gif")
        )
    }

    @Test
    fun Gfycat() {
        assertEquals(
            "gfycat=GFYCATID",
            mapper.getGifPart("https://gfycat.com/GFYCATID")
        )
        assertEquals(
            "gfycat=GFYCATID",
            mapper.getGifPart("https://gfycat.com/GFYCATID.mp4")
        )
        assertEquals(
            "gfycat=GFYCATID",
            mapper.getGifPart("https://gfycat.com/GFYCATID.gif")
        )
    }
}
