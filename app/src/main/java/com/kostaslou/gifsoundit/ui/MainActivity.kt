package com.kostaslou.gifsoundit.ui

import android.os.Bundle
import com.kostaslou.gifsoundit.R
import com.kostaslou.gifsoundit.ui.home.HomeFragment
import dagger.android.support.DaggerAppCompatActivity

class MainActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction().add(R.id.fragContainer, HomeFragment()).commit()
    }
}
