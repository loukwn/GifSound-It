package com.kostaslou.gifsoundit.screens

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.loukwn.navigation.Navigator
import com.kostaslou.gifsoundit.R
import com.kostaslou.gifsoundit.list.controller.ListFragment
import com.kostaslou.gifsoundit.opengs.controller.OpenGSFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // deep linking stuff
        navigator.bind(findNavController(R.id.nav_host_fragment))

        val externalIntentQuery = intent?.data?.query ?: ""
        if (externalIntentQuery.isNotEmpty()) {
            // if we come from another app with data

            navigator.navigateToOpenGS(externalIntentQuery)
        }
    }

    override fun onResume() {
        super.onResume()

        navigator.bind(findNavController(R.id.nav_host_fragment))
    }

    override fun onPause() {
        super.onPause()

        navigator.unbind()
    }

    override fun onDestroy() {
        navigator.reset()
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.nav_host_fragment).navigateUp()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handling of option menu clicks.

        when (item.itemId) {
            R.id.action_about -> {
                navigator.navigateToSettings()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
