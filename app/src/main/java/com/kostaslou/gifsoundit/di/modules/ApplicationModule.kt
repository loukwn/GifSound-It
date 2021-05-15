package com.kostaslou.gifsoundit.di.modules

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("reddit_stuff", Context.MODE_PRIVATE)

    @Provides
    fun provideResources(@ApplicationContext context: Context): Resources =
        context.resources
}
