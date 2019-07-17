package com.kostaslou.gifsoundit.data

import com.kostaslou.gifsoundit.BuildConfig
import com.kostaslou.gifsoundit.commons.PostType
import com.kostaslou.gifsoundit.commons.RedditPostResponse
import com.kostaslou.gifsoundit.commons.RedditTokenResponse
import com.kostaslou.gifsoundit.data.api.AuthApi
import com.kostaslou.gifsoundit.data.api.BasicRedditAuthInterceptor
import com.kostaslou.gifsoundit.data.api.PostApi
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

// the main api with the initializer
class Repository {
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
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())

        // the api clients
        authApi = retrofit.baseUrl("https://www.reddit.com/").client(client).build().create(AuthApi::class.java)
        postApi = retrofit.baseUrl("https://www.oauth.reddit.com/").client(client2).build().create(PostApi::class.java)
    }

    fun getAuthToken(): Single<RedditTokenResponse> {
        return authApi.getAuthToken("https://oauth.reddit.com/grants/installed_client", UUID.randomUUID().toString())
    }

    fun getPosts(accessToken: String, deviceType: Int, after: String, topType: String = "all"): Single<RedditPostResponse> {
        return when(deviceType){
            PostType.TOP -> postApi.getTopGifSounds("bearer $accessToken", BuildConfig.RedditUserAgent, after, topType)
            PostType.NEW -> postApi.getNewGifSounds("bearer $accessToken", BuildConfig.RedditUserAgent, after)
            else -> postApi.getHotGifSounds("bearer $accessToken", BuildConfig.RedditUserAgent, after)

        }
    }
}