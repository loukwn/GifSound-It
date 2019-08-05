package com.kostaslou.gifsoundit.di.modules

import com.kostaslou.gifsoundit.home.ui.HomeFragment
import com.kostaslou.gifsoundit.opengs.OpenGSFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBindingModule {

    @ContributesAndroidInjector
    internal abstract fun provideHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    internal abstract fun provideOpenGSFragment(): OpenGSFragment
//
//    @ContributesAndroidInjector
//    internal abstract fun provideDetailsFragment(): GSCreateFragment
}
