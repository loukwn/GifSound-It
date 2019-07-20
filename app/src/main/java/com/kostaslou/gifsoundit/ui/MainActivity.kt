package com.kostaslou.gifsoundit.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.kostaslou.gifsoundit.R
import com.kostaslou.gifsoundit.data.disk.SharedPrefsHelper
import com.kostaslou.gifsoundit.ui.home.HomeFragment
import com.mikepenz.aboutlibraries.LibsBuilder
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var sharedPrefsHelper: SharedPrefsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPrefsHelper.deleteSavedData(SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN)
        sharedPrefsHelper.deleteSavedData(SharedPrefsHelper.PREF_KEY_EXPIRES_AT)

        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction().add(R.id.fragContainer, HomeFragment()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handling of option menu clicks.

        when (item.itemId) {
            R.id.action_about -> {

                // show about section
                LibsBuilder()
                    .withAboutAppName(getString(R.string.app_name))
                    .withAboutIconShown(true)
                    .withAboutVersionShown(true)
                    .withAboutDescription(getString(R.string.about_libraries_description))
                    .start(this)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
