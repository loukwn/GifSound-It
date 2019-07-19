package com.kostaslou.gifsoundit.di.modules

import com.kostaslou.gifsoundit.ui.home.HomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class MainFragmentBindingModule {

    @ContributesAndroidInjector
    internal abstract fun provideHomeFragment(): HomeFragment
//
//    @ContributesAndroidInjector
//    internal abstract fun provideDetailsFragment(): GSDetailFragment
//
//    @ContributesAndroidInjector
//    internal abstract fun provideDetailsFragment(): GSCreateFragment
}