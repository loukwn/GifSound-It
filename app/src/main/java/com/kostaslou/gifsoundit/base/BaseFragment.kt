package com.kostaslou.gifsoundit.base

import android.content.Context
import dagger.android.support.DaggerFragment
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.NonNull


abstract class BaseFragment : DaggerFragment() {

    private var activity: AppCompatActivity? = null

    @LayoutRes
    protected abstract fun layoutRes(): Int

    override fun onCreateView(@NonNull inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutRes(), container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        activity = context as AppCompatActivity
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    fun getBaseActivity(): AppCompatActivity? {
        return activity
    }
}