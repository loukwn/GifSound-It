package com.kostaslou.gifsoundit.postdata.network

import com.kostaslou.gifsoundit.postdata.model.api.RedditSubredditResponse
import com.kostaslou.gifsoundit.postdata.model.api.RedditTokenResponse
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// The endpoint interfaces
internal interface AuthApi {
    @FormUrlEncoded
    @POST("/api/v1/access_token")
    fun getAuthToken(
        @Field("grant_type") grant_type: String,
        @Field("device_id") device_id: String
    ): Single<RedditTokenResponse>
}

internal interface PostApi {
    // Hot posts
    @GET("/r/{subName}/hot?raw_json=1")
    fun getHotGifSounds(
        @Path("subName") subName: String,
        @Header("Authorization") tokenData: String,
        @Header("User-Agent") userAgent: String,
        @Query("after") after: String
    ): Single<RedditSubredditResponse>

    // New posts
    @GET("/r/{subName}/new?raw_json=1")
    fun getNewGifSounds(
        @Path("subName") subName: String,
        @Header("Authorization") tokenData: String,
        @Header("User-Agent") userAgent: String,
        @Query("after") after: String
    ): Single<RedditSubredditResponse>

    // Top posts
    @GET("/r/{subName}/top?raw_json=1")
    fun getTopGifSounds(
        @Path("subName") subName: String,
        @Header("Authorization") tokenData: String,
        @Header("User-Agent") userAgent: String,
        @Query("after") after: String,
        @Query("t") time: String = "all"
    ): Single<RedditSubredditResponse>
}
