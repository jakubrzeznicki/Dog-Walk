package com.kuba.dogwalk.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.kuba.dogwalk.R
import com.kuba.dogwalk.databinding.ActivityMainBinding
import com.kuba.dogwalk.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.kuba.dogwalk.other.Constants.NOTIFICATION
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        navigateToFragmentsIfNeeded(intent)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController
            .addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.notificationViewPagerFragment, R.id.myWalkListFragment, R.id.caloriesCalculatorFragment,
                    R.id.profileFragment -> binding.bottomNavigationView.visibility = View.VISIBLE

                    else -> binding.bottomNavigationView.visibility = View.GONE
                }
            }

        binding.bottomNavigationView.setupWithNavController(navController)

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToFragmentsIfNeeded(intent)
    }

    private fun navigateToFragmentsIfNeeded(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController.navigate(R.id.action_global_trackingWalkFragment)
        }

        if (intent?.getBooleanExtra(NOTIFICATION, false)!!) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController.navigate(R.id.action_global_myListWalkFragment)
        }
    }
}