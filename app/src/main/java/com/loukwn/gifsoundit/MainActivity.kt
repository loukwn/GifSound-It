package com.loukwn.gifsoundit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.loukwn.gifsoundit.navigation.Navigator
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigator.bind(context = this, navController = getNavController())
        if (savedInstanceState == null) {
            Timber.d("onCreate null")
            navigateIfDeepLinked(intent)
        }
    }

    private fun navigateIfDeepLinked(intent: Intent?) {
        intent?.data?.query?.let {
            if (it.isNotEmpty() && intent.action == Intent.ACTION_VIEW) {
                navigator.clearBackStack()
                navigator.navigateToOpenGS(
                    query = "https://gifsound.com/?$it",
                    fromDeepLink = true
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Timber.d("onNewIntent")

        navigator.bind(context = this, navController = findNavController(R.id.nav_host_fragment))
        navigateIfDeepLinked(intent)
    }

    private fun getNavController(): NavController =
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
            .navController

    override fun onResume() {
        super.onResume()
        navigator.bind(context = this, navController = findNavController(R.id.nav_host_fragment))
    }

    override fun onPause() {
        super.onPause()
        navigator.unbind()
    }
}
