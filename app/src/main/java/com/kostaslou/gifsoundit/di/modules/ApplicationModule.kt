package com.kostaslou.gifsoundit.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.kostaslou.gifsoundit.BuildConfig
import com.kostaslou.gifsoundit.commons.RedditConstants
import com.kostaslou.gifsoundit.data.api.AuthApi
import com.kostaslou.gifsoundit.data.api.BasicRedditAuthInterceptor
import com.kostaslou.gifsoundit.data.api.PostApi
import com.kostaslou.gifsoundit.util.RxSchedulers
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class ApplicationModule {
    @Singleton
    @Provides
    fun provideRetrofitBuilder(): Retrofit.Builder =
            Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create())

    @Singleton
    @Provides
    @Named("auth_client")
    fun provideAuthClient(): OkHttpClient =
            OkHttpClient.Builder()
                    .addInterceptor(BasicRedditAuthInterceptor(BuildConfig.RedditClientId, ""))
                    .build()

    @Singleton
    @Provides
    @Named("post_client")
    // for some reason retrofit2 complains about the certificates of oauth.reddit.com
    fun providePostClient(): OkHttpClient =
            OkHttpClient.Builder().hostnameVerifier { _, _ -> true }.build()

    @Singleton
    @Provides
    fun provideAuthApi(retrofitBuilder: Retrofit.Builder, @Named("auth_client") authClient: OkHttpClient): AuthApi =
            retrofitBuilder.baseUrl(RedditConstants.REDDIT_AUTH_BASE_URL).client(authClient).build().create(AuthApi::class.java)

    @Singleton
    @Provides
    fun providePostApi(retrofitBuilder: Retrofit.Builder, @Named("post_client") postClient: OkHttpClient): PostApi =
            retrofitBuilder.baseUrl(RedditConstants.REDDIT_POST_BASE_URL).client(postClient).build().create(PostApi::class.java)

    @Provides
    fun provideRxSchedulers() : RxSchedulers = RxSchedulers.default()

    @Singleton
    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences =  application.getSharedPreferences("reddit_stuff", Context.MODE_PRIVATE)
}