package com.kostaslou.gifsoundit.api

import com.kostaslou.gifsoundit.BuildConfig
import com.kostaslou.gifsoundit.commons.PostType
import com.kostaslou.gifsoundit.commons.RedditPostResponse
import com.kostaslou.gifsoundit.commons.RedditTokenResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.*

// The endpoint interfaces
interface AuthApi {
    @FormUrlEncoded
    @POST("/api/v1/access_token")
    fun getToken(@Field("grant_type") grant_type: String,
                 @Field("device_id") device_id: String)
            : Call<RedditTokenResponse>
}

interface PostApi {
    // Hot gifsounds
    @GET("/r/GifSound/hot?raw_json=1")
    fun getHotGifSounds(@Header("Authorization") tokenData: String,
                    @Header("User-Agent") userAgent: String,
                    @Query("after") after: String)
            : Call<RedditPostResponse>

    // New gifsounds
    @GET("/r/GifSound/new?raw_json=1")
    fun getNewGifSounds(@Header("Authorization") tokenData: String,
                        @Header("User-Agent") userAgent: String,
                        @Query("after") after: String)
            : Call<RedditPostResponse>

    // Top Gifsounds
    @GET("/r/GifSound/top?raw_json=1")
    fun getTopGifSounds(@Header("Authorization") tokenData: String,
                        @Header("User-Agent") userAgent: String,
                        @Query("after") after: String,
                        @Query("t") time: String = "all")
            : Call<RedditPostResponse>
}

// the main api with the initializer
class RestAPI {
    private val authApi: AuthApi
    private val postApi: PostApi

    init {
        // interceptor for the login
        val client = OkHttpClient.Builder()
                .addInterceptor(BasicRedditAuthInterceptor(BuildConfig.RedditClientId, ""))
                .build()

        // for some reason retrofit2 complains about the certificates of oauth.reddit.com
        val client2 = OkHttpClient.Builder().hostnameVerifier { _, _ -> true }.build()

        // main retrofit object
        val retrofit = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())

        // the api clients
        authApi = retrofit.baseUrl("https://www.reddit.com/").client(client).build().create(AuthApi::class.java)
        postApi = retrofit.baseUrl("https://www.oauth.reddit.com/").client(client2).build().create(PostApi::class.java)
    }

    fun getAuthToken(): Call<RedditTokenResponse> {
        return authApi.getToken("https://oauth.reddit.com/grants/installed_client", UUID.randomUUID().toString())
    }

    fun getPosts(accessToken: String, deviceType: Int, after: String, topType: String = "all"): Call<RedditPostResponse>? {
        when(deviceType){
            PostType.HOT -> return postApi.getHotGifSounds("bearer $accessToken", BuildConfig.RedditUserAgent, after)
            PostType.TOP -> return postApi.getTopGifSounds("bearer $accessToken", BuildConfig.RedditUserAgent, after, topType)
            PostType.NEW -> return postApi.getNewGifSounds("bearer $accessToken", BuildConfig.RedditUserAgent, after)

        }
        return null
    }
}