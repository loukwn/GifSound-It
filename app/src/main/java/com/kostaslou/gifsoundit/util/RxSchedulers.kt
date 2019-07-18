package com.kostaslou.gifsoundit.util

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class RxSchedulers /*@Inject*/ constructor(/*@Named("io")*/ val ioScheduler: Scheduler, /*@Named("android")*/ val androidScheduler: Scheduler){
    companion object {
        fun test() : RxSchedulers {
            return RxSchedulers(Schedulers.trampoline(), Schedulers.trampoline())
        }
    }
}