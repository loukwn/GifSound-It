package com.kostaslou.gifsoundit.util

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Named

class RxSchedulers @Inject constructor(
    @Named("io") val ioScheduler: Scheduler,
    @Named("android") val androidScheduler: Scheduler
) {
    companion object {
        fun test(): RxSchedulers {
            return RxSchedulers(Schedulers.trampoline(), Schedulers.trampoline())
        }

        fun default(): RxSchedulers {
            return RxSchedulers(Schedulers.io(), AndroidSchedulers.mainThread())
        }
    }
}