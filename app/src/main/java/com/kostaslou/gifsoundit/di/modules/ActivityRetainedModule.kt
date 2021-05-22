package com.kostaslou.gifsoundit.di.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Named

@Module
@InstallIn(ActivityRetainedComponent::class)
class ActivityRetainedModule {

    @Provides
    @Named("io")
    fun provideIOScheduler(): Scheduler = Schedulers.io()

    @Provides
    @Named("ui")
    fun provideUIScheduler(): Scheduler = AndroidSchedulers.mainThread()

    @Provides
    @Named("computation")
    fun provideComputationScheduler(): Scheduler = Schedulers.computation()
}
