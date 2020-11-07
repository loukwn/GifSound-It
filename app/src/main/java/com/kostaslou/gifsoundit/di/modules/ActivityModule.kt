package com.kostaslou.gifsoundit.di.modules

import com.kostaslou.gifsoundit.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {

    @Provides
    fun provideNavigator(): Navigator = Navigator()
}
