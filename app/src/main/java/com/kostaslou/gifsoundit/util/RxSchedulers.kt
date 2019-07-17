package com.kostaslou.gifsoundit.util

import io.reactivex.Scheduler

class RxSchedulers /*@Inject*/ constructor(/*@Named("io")*/ val ioScheduler: Scheduler, /*@Named("android")*/ val androidScheduler: Scheduler)