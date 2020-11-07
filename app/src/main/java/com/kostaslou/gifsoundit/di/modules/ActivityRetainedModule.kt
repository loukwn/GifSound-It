package com.kostaslou.gifsoundit.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.kostaslou.gifsoundit.home.BuildConfig
import com.kostaslou.gifsoundit.home.data.api.AuthApi
import com.kostaslou.gifsoundit.home.data.api.BasicRedditAuthInterceptor
import com.kostaslou.gifsoundit.home.data.api.PostApi
import com.kostaslou.gifsoundit.home.util.RxSchedulers
import com.kostaslou.gifsoundit.home.util.commons.RedditConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
class ActivityRetainedModule {
    @Provides
    fun provideRetrofitBuilder(): Retrofit.Builder =
            Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create())

    @Provides
    @Named("auth_client")
    fun provideAuthClient(): OkHttpClient =
            OkHttpClient.Builder()
                    .addInterceptor(BasicRedditAuthInterceptor(BuildConfig.RedditClientId, ""))
                    .build()

    @Provides
    @Named("post_client")
    // for some reason retrofit2 complains about the certificates of oauth.reddit.com
    fun providePostClient(): OkHttpClient =
            OkHttpClient.Builder().hostnameVerifier { _, _ -> true }.build()

    @Provides
    fun provideAuthApi(retrofitBuilder: Retrofit.Builder, @Named("auth_client") authClient: OkHttpClient): AuthApi =
            retrofitBuilder.baseUrl(RedditConstants.REDDIT_AUTH_BASE_URL).client(authClient).build().create(AuthApi::class.java)

    @Provides
    fun providePostApi(retrofitBuilder: Retrofit.Builder, @Named("post_client") postClient: OkHttpClient): PostApi =
            retrofitBuilder.baseUrl(RedditConstants.REDDIT_POST_BASE_URL).client(postClient).build().create(PostApi::class.java)

    @Provides
    fun provideRxSchedulers(): RxSchedulers = RxSchedulers.default()
}
