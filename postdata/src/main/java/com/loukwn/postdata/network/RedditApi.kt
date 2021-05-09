package com.loukwn.postdata.network

import com.loukwn.postdata.model.api.RedditSubredditResponse
import com.loukwn.postdata.model.api.RedditTokenResponse
import io.reactivex.Single
import retrofit2.http.*

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
