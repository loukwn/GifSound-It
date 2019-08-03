package com.kostaslou.gifsoundit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kostaslou.gifsoundit.ui.open.OpenGSViewModel
import com.kostaslou.gifsoundit.ui.open.util.GifUrl
import com.kostaslou.gifsoundit.ui.open.util.SoundUrl
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import kotlin.test.assertEquals

class OpenGSViewModelTest {

    // instant execution of livedata operations
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    // the viewModel under test
    private lateinit var viewModel: OpenGSViewModel

    @Before
    fun setup() {
        viewModel = OpenGSViewModel()
    }

    @Test
    fun `when a gifsound with no second offset is opened`() {
        // given that the gifsound is opened...
        val query = "mp4=https%3A%2F%2Fi.imgur.com%2F1rWYUyN&v=4sCXkpZsBRg"
        viewModel.setGifSoundArgs(query)

        // ...assert that seconds (offset) are set accordingly
        assertEquals(0, viewModel.secondOffsetLiveData.value)
    }

    @Test
    fun `when an imgur mp4 gifsound is opened`() {

        // given that the gifsound is opened...
        val query = "mp4=https%3A%2F%2Fi.imgur.com%2F1rWYUyN&v=4sCXkpZsBRg&s=175"
        viewModel.setGifSoundArgs(query)

        // ...assert that the gif, sound and seconds (offset) are set accordingly
        assertEquals(175, viewModel.secondOffsetLiveData.value)
        assertEquals(SoundUrl("4sCXkpZsBRg"), viewModel.soundUrlLiveData.value)
        assertEquals(GifUrl("https%3A%2F%2Fi.imgur.com%2F1rWYUyN.mp4", GifUrl.GifType.MP4), viewModel.gifUrlLiveData.value)
    }

    @Test
    fun `when a giphy gif gifsound is opened`() {

        // given that the gifsound is opened...
        val query = "gif=https%3A%2F%2Fmedia.giphy.com%2Fmedia%2F3o6gDWzmAzrpi5DQU8%2Fgiphy.gif&v=zoP4h3Hv4Uk&s=38"
        viewModel.setGifSoundArgs(query)

        // ...assert that the gif, sound and seconds (offset) are set accordingly
        assertEquals(38, viewModel.secondOffsetLiveData.value)
        assertEquals(SoundUrl("zoP4h3Hv4Uk"), viewModel.soundUrlLiveData.value)
        assertEquals(GifUrl("https://media.giphy.com/media/3o6gDWzmAzrpi5DQU8/giphy.gif", GifUrl.GifType.GIF), viewModel.gifUrlLiveData.value)
    }

    @Test
    fun `when a gfycat webm gifsound is opened`() {

        // given that the gifsound is opened...
        val query = "webm=https%3A%2F%2Fgiant.gfycat.com%2FFarflungFewIndri&v=zHalXjs0cDA&s=286"
        viewModel.setGifSoundArgs(query)

        // ...assert that the gif, sound and seconds (offset) are set accordingly
        assertEquals(286, viewModel.secondOffsetLiveData.value)
        assertEquals(SoundUrl("zHalXjs0cDA"), viewModel.soundUrlLiveData.value)
        assertEquals(GifUrl("https://giant.gfycat.com/FarflungFewIndri.webm", GifUrl.GifType.MP4), viewModel.gifUrlLiveData.value)
    }

    @Test
    fun `when an imgur gifv gifsound is opened`() {

        // given that the gifsound is opened...
        val query = "gifv=ZefKQD3&v=VYPsoxpt0BU&s=9"
        viewModel.setGifSoundArgs(query)

        // ...assert that the gif, sound and seconds (offset) are set accordingly
        assertEquals(9, viewModel.secondOffsetLiveData.value)
        assertEquals(SoundUrl("VYPsoxpt0BU"), viewModel.soundUrlLiveData.value)
        assertEquals(GifUrl("http://i.imgur.com/ZefKQD3.mp4", GifUrl.GifType.MP4), viewModel.gifUrlLiveData.value)
    }
}
