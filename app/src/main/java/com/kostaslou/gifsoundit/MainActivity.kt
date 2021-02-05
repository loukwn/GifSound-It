package com.kostaslou.gifsoundit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.loukwn.navigation.Navigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigator.bind(context = this, navController = getNavController())
        navigateIfDeepLinked(intent)
    }

    private fun navigateIfDeepLinked(intent: Intent?) {
        intent?.data?.query?.let {
            if (it.isNotEmpty()) navigator.navigateToOpenGS(query = it, fromDeepLink = true)
        }
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
