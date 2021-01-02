package com.kostaslou.gifsoundit.di.modules

import com.kostaslou.gifsoundit.BuildConfig
import com.loukwn.navigation.Navigator
import com.loukwn.postdata.RedditConstants
import com.loukwn.postdata.network.AuthApi
import com.loukwn.postdata.network.BasicRedditAuthInterceptor
import com.loukwn.postdata.network.PostApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named

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
    fun provideAuthClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor(BasicRedditAuthInterceptor(BuildConfig.RedditClientId, ""))
            .build()
    }

    @Provides
    @Named("post_client")
    // for some reason retrofit2 complains about the certificates of oauth.reddit.com
    fun providePostClient(): OkHttpClient {

        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    @Provides
    fun provideAuthApi(
        retrofitBuilder: Retrofit.Builder,
        @Named("auth_client") authClient: OkHttpClient
    ): AuthApi =
        retrofitBuilder
            .baseUrl(RedditConstants.REDDIT_AUTH_BASE_URL)
            .client(authClient)
            .build()
            .create(AuthApi::class.java)

    @Provides
    fun providePostApi(
        retrofitBuilder: Retrofit.Builder,
        @Named("post_client") postClient: OkHttpClient
    ): PostApi =
        retrofitBuilder
            .baseUrl(RedditConstants.REDDIT_POST_BASE_URL)
            .client(postClient)
            .build()
            .create(PostApi::class.java)

    @Provides
    @Named("io")
    fun provideIOScheduler(): Scheduler = Schedulers.io()

    @Provides
    @Named("ui")
    fun provideUIScheduler(): Scheduler = AndroidSchedulers.mainThread()

    @ActivityRetainedScoped
    @Provides
    fun provideNavigator(): Navigator = Navigator()
}
