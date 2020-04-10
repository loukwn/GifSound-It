package com.kostaslou.gifsoundit.di.modules

import com.kostaslou.gifsoundit.home.ui.HomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBindingModule {

    @ContributesAndroidInjector
    internal abstract fun provideHomeFragment(): HomeFragment
}
