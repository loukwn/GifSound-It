package com.kostaslou.gifsoundit.data.api

import com.kostaslou.gifsoundit.data.api.model.RedditPostResponse
import com.kostaslou.gifsoundit.data.api.model.RedditTokenResponse
import io.reactivex.Single
import retrofit2.http.*

// The endpoint interfaces
interface AuthApi {
    @FormUrlEncoded
    @POST("/api/v1/access_token")
    fun getAuthToken(
        @Field("grant_type") grant_type: String,
        @Field("device_id") device_id: String
    ):
            Single<RedditTokenResponse>
}

interface PostApi {
    // Hot gifsounds
    @GET("/r/GifSound/hot?raw_json=1")
    fun getHotGifSounds(
        @Header("Authorization") tokenData: String,
        @Header("User-Agent") userAgent: String,
        @Query("after") after: String
    ):
            Single<RedditPostResponse>

    // New gifsounds
    @GET("/r/GifSound/new?raw_json=1")
    fun getNewGifSounds(
        @Header("Authorization") tokenData: String,
        @Header("User-Agent") userAgent: String,
        @Query("after") after: String
    ):
            Single<RedditPostResponse>

    // Top Gifsounds
    @GET("/r/GifSound/top?raw_json=1")
    fun getTopGifSounds(
        @Header("Authorization") tokenData: String,
        @Header("User-Agent") userAgent: String,
        @Query("after") after: String,
        @Query("t") time: String = "all"
    ):
            Single<RedditPostResponse>
}
