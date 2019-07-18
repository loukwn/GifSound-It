package com.kostaslou.gifsoundit.di.modules

import com.kostaslou.gifsoundit.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector(modules = [MainFragmentBindingModule::class])
    internal abstract fun bindMainActivity(): MainActivity
}