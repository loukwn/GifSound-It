@file:Suppress("unused")

package com.kostaslou.gifsoundit.navigation.di

import com.kostaslou.gifsoundit.navigation.Navigator
import com.kostaslou.gifsoundit.navigation.NavigatorImpl
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
