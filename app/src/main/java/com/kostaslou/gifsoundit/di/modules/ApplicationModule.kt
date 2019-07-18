package com.kostaslou.gifsoundit.di.modules

import dagger.Module
import retrofit2.Retrofit
import dagger.Provides
import javax.inject.Singleton
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory


@Module
object ApplicationModule {

//    @Singleton
//    @Provides
//    internal fun provideRetrofit(): Retrofit {
//        return Retrofit.Builder().baseUrl(BASE_URL)
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(MoshiConverterFactory.create())
//                .build()
//    }
//
//    @Singleton
//    @Provides
//    internal fun provideRetrofitService(retrofit: Retrofit): RepoService {
//        return retrofit.create<RepoService>(RepoService::class.java!!)
//    }
}