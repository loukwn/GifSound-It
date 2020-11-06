package com.kostaslou.gifsoundit.screens

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.findNavController
import com.kostaslou.gifsoundit.Navigator
import com.kostaslou.gifsoundit.R
import com.kostaslou.gifsoundit.home.ui.HomeFragment
import com.kostaslou.gifsoundit.opengs.controller.OpenGSFragment
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : DaggerAppCompatActivity(), HomeFragment.Callback, OpenGSFragment.Callback {

    private var navigator = Navigator()

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

    override fun onSupportNavigateUp(): Boolean = findNavController(R.id.nav_host_fragment).navigateUp()

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

    override fun navigateToOpenGS(query: String?) {
        navigator.navigateToOpenGS(query)
    }

    override fun onOpenGSBackPressed() {
        onBackPressed()
    }
}
