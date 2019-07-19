package com.kostaslou.gifsoundit.ui.home

import androidx.lifecycle.ViewModel
import com.kostaslou.gifsoundit.data.Repository
import com.kostaslou.gifsoundit.util.RxSchedulers
import javax.inject.Inject


class HomeViewModel @Inject constructor(private val repository: Repository,
                                        private val rxSchedulers: RxSchedulers) : ViewModel() {

    fun showHello(): String = "Hello from your new ViewHolder!"
}