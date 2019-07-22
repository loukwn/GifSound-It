package com.kostaslou.gifsoundit.data

import com.kostaslou.gifsoundit.BuildConfig
import com.kostaslou.gifsoundit.util.commons.PostType
import com.kostaslou.gifsoundit.util.commons.RedditConstants
import com.kostaslou.gifsoundit.data.api.AuthApi
import com.kostaslou.gifsoundit.data.api.PostApi
import com.kostaslou.gifsoundit.data.api.model.RedditPostResponse
import com.kostaslou.gifsoundit.data.api.model.RedditTokenResponse
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

// the main api with the initializer
class Repository @Inject constructor(private val authApi: AuthApi, private val postApi: PostApi) {

    fun getRedditAuthToken(): Single<RedditTokenResponse> {
        return authApi.getAuthToken(RedditConstants.REDDIT_GRANT_TYPE, UUID.randomUUID().toString())
    }

    fun getPostsFromNetwork(accessToken: String, postType: PostType, after: String, topType: String = "all"): Single<RedditPostResponse> {
        return when(postType){
            PostType.TOP -> postApi.getTopGifSounds("bearer $accessToken", BuildConfig.RedditUserAgent, after, topType)
            PostType.NEW -> postApi.getNewGifSounds("bearer $accessToken", BuildConfig.RedditUserAgent, after)
            PostType.HOT -> postApi.getHotGifSounds("bearer $accessToken", BuildConfig.RedditUserAgent, after)
        }
    }
}