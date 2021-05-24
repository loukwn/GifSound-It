package com.kostaslou.gifsoundit.postdata

import com.kostaslou.gifsoundit.common.disk.SharedPrefsHelper
import com.kostaslou.gifsoundit.postdata.network.AuthApi
import com.kostaslou.gifsoundit.postdata.network.PostApi
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import java.util.Date

internal class PostRepositoryImplTest {

    private val authApi = mockk<AuthApi>(relaxed = true)
    private val postApi = mockk<PostApi>(relaxed = true)
    private val sharedPreferences = mockk<SharedPrefsHelper>(relaxed = true)
    private val ioScheduler = Schedulers.trampoline()

    private val sut: PostRepository = PostRepositoryImpl(
        authApi = authApi,
        postApi = postApi,
        sharedPrefsHelper = sharedPreferences,
        ioScheduler = ioScheduler
    )

    @Test
    fun `GIVEN token not saved in sharedPrefs WHEN refreshAuthTokenIfNeeded THEN refresh token`() {
        every { sharedPreferences[SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, ""] } returns ""
        sut.refreshAuthTokenIfNeeded()
        verify(exactly = 1) { authApi.getAuthToken(any(), any()) }
    }

    @Test
    fun `GIVEN token saved in sharedPrefs AND expiresAtDate is later than now WHEN refreshAuthTokenIfNeeded THEN should not refresh token`() {
        val later = Date().time + 1 * 60 * 1000 // 1 minute from now
        every { sharedPreferences[SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, ""] } returns "token"
        every { sharedPreferences[SharedPrefsHelper.PREF_KEY_EXPIRES_AT, any<Long>()] } returns later
        sut.refreshAuthTokenIfNeeded()
        verify(exactly = 0) { authApi.getAuthToken(any(), any()) }
    }

    @Test
    fun `GIVEN token saved in sharedPrefs AND expiresAtDate is before now WHEN refreshAuthTokenIfNeeded THEN should refresh token`() {
        val later = Date().time - 1 * 60 * 1000 // 1 minute before now
        every { sharedPreferences[SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, ""] } returns "token"
        every { sharedPreferences[SharedPrefsHelper.PREF_KEY_EXPIRES_AT, any<Long>()] } returns later
        sut.refreshAuthTokenIfNeeded()
        verify(exactly = 1) { authApi.getAuthToken(any(), any()) }
    }

    @Test
    fun `GIVEN token is not valid WHEN getPosts() THEN get a new token and then do the request`() {
        every { sharedPreferences[SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, ""] } returns ""
        every { authApi.getAuthToken(any(), any()) } returns Single.just(
            mockk(relaxed = true) {
                every { expires_in } returns 0L.toString()
            }
        )
        sut.getPosts(
            sourceType = mockk(relaxed = true),
            filterType = FilterTypeDTO.Hot,
            after = ""
        )
        verify(exactly = 1) { authApi.getAuthToken(any(), any()) }
        verify(exactly = 1) { postApi.getHotGifSounds(any(), any(), any(), any()) }
    }

    @Test
    fun `GIVEN token is valid WHEN getPosts(Hot) THEN just do the post request for hot`() {
        val later = Date().time + 1 * 60 * 1000 // 1 minute from now
        every { sharedPreferences[SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, ""] } returns "token"
        every { sharedPreferences[SharedPrefsHelper.PREF_KEY_EXPIRES_AT, any<Long>()] } returns later
        sut.getPosts(
            sourceType = mockk(relaxed = true),
            filterType = FilterTypeDTO.Hot,
            after = ""
        )
        verify(exactly = 0) { authApi.getAuthToken(any(), any()) }
        verify(exactly = 1) { postApi.getHotGifSounds(any(), any(), any(), any()) }
    }

    @Test
    fun `GIVEN token is valid WHEN getPosts(New) THEN just do the post request for new`() {
        val later = Date().time + 1 * 60 * 1000 // 1 minute from now
        every { sharedPreferences[SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, ""] } returns "token"
        every { sharedPreferences[SharedPrefsHelper.PREF_KEY_EXPIRES_AT, any<Long>()] } returns later
        sut.getPosts(
            sourceType = mockk(relaxed = true),
            filterType = FilterTypeDTO.New,
            after = ""
        )
        verify(exactly = 0) { authApi.getAuthToken(any(), any()) }
        verify(exactly = 1) { postApi.getNewGifSounds(any(), any(), any(), any()) }
    }

    @Test
    fun `GIVEN token is valid WHEN getPosts(Top) THEN just do the post request for top`() {
        val later = Date().time + 1 * 60 * 1000 // 1 minute from now
        every { sharedPreferences[SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, ""] } returns "token"
        every { sharedPreferences[SharedPrefsHelper.PREF_KEY_EXPIRES_AT, any<Long>()] } returns later
        sut.getPosts(
            sourceType = mockk(relaxed = true),
            filterType = FilterTypeDTO.Top(mockk(relaxed = true)),
            after = ""
        )
        verify(exactly = 0) { authApi.getAuthToken(any(), any()) }
        verify(exactly = 1) { postApi.getTopGifSounds(any(), any(), any(), any(), any()) }
    }
}
