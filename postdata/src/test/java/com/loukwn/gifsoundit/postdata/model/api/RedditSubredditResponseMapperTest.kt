package com.loukwn.gifsoundit.postdata.model.api

import com.loukwn.gifsoundit.postdata.PostRepositoryImpl.Companion.NUM_OF_POSTS_PER_REQUEST
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class RedditSubredditResponseMapperTest {

    @Test
    fun `WHEN NUM_OF_POSTS_PER_REQUEST posts in response and some are nsfw THEN we can request for more`() {
        val response = RedditSubredditResponse(
            RedditSubredditDataResponse(
                children = ArrayList<RedditPostChild>().apply {
                    for (i in 0 until NUM_OF_POSTS_PER_REQUEST) {
                        val isNsfw = i % 2 == 0
                        add(
                            mockk(relaxed = true) {
                                every { data.over_18 } returns isNsfw
                            }
                        )
                    }
                },
                after = null
            )
        )

        val domainResponse = response.toDomainData()

        assertTrue(domainResponse.canFetchMore)
    }

    @Test
    fun `WHEN NUM_OF_POSTS_PER_REQUEST posts in response and none are nsfw THEN we can request for more`() {
        val response = RedditSubredditResponse(
            RedditSubredditDataResponse(
                children = ArrayList<RedditPostChild>().apply {
                    for (i in 0 until NUM_OF_POSTS_PER_REQUEST) {
                        add(
                            mockk(relaxed = true) {
                                every { data.over_18 } returns false
                            }
                        )
                    }
                },
                after = null
            )
        )

        val domainResponse = response.toDomainData()

        assertTrue(domainResponse.canFetchMore)
    }

    @Test
    fun `WHEN less than NUM_OF_POSTS_PER_REQUEST posts in response THEN we cannot request for more`() {
        val response = RedditSubredditResponse(
            RedditSubredditDataResponse(
                children = ArrayList<RedditPostChild>().apply {
                    for (i in 0 until NUM_OF_POSTS_PER_REQUEST - 1) {
                        add(mockk(relaxed = true))
                    }
                },
                after = null
            )
        )

        val domainResponse = response.toDomainData()

        assertFalse(domainResponse.canFetchMore)
    }

    @Test
    fun `WHEN childs url starts with r THEN make it start with the full url`() {
        val response = RedditSubredditResponse(
            RedditSubredditDataResponse(
                children = arrayListOf(
                    mockk(relaxed = true) {
                        every { data.url } returns "/r/GifSoundIt"
                    }
                ),
                after = null
            )
        )

        val domainResponse = response.toDomainData()

        assertEquals("http://www.reddit.com/r/GifSoundIt", domainResponse.postData[0].url)
    }

    @Test
    fun `WHEN childs permalink starts with r THEN make it start with the full permalink`() {
        val response = RedditSubredditResponse(
            RedditSubredditDataResponse(
                children = arrayListOf(
                    mockk(relaxed = true) {
                        every { data.permalink } returns "/r/GifSoundIt"
                    }
                ),
                after = null
            )
        )

        val domainResponse = response.toDomainData()

        assertEquals("http://www.reddit.com/r/GifSoundIt", domainResponse.postData[0].permalink)
    }
}
