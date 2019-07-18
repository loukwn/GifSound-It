package com.kostaslou.gifsoundit.di

import android.app.Application
import com.kostaslou.gifsoundit.GifSoundItApp
import com.kostaslou.gifsoundit.di.modules.ActivityBindingModule
import com.kostaslou.gifsoundit.di.modules.ApplicationModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import javax.inject.Singleton


@Singleton
@Component(modules = [ApplicationModule::class, AndroidSupportInjectionModule::class, ActivityBindingModule::class ])
interface ApplicationComponent : AndroidInjector<DaggerApplication> {

    fun inject(application: GifSoundItApp)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }
}