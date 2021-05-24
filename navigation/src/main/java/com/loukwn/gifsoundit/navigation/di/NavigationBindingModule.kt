@file:Suppress("unused")

package com.loukwn.gifsoundit.navigation.di

import com.loukwn.gifsoundit.navigation.Navigator
import com.loukwn.gifsoundit.navigation.NavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
internal abstract class NavigationBindingModule {

    @Binds
    abstract fun bindNavigator(impl: NavigatorImpl): Navigator
}
