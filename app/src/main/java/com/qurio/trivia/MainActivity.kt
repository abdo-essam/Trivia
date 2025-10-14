package com.qurio.trivia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.qurio.trivia.databinding.ActivityMainBinding
import com.qurio.trivia.utils.PreferenceKeys.IS_FIRST_LAUNCH

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        installSplashScreen()
        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val isFirstLaunch = intent.getBooleanExtra(
            IS_FIRST_LAUNCH,
            true
        )

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        val startDestination = if (isFirstLaunch) {
            R.id.onboardingFragment
        } else {
            R.id.homeFragment
        }

        navGraph.setStartDestination(startDestination)
        navController.graph = navGraph
    }
}
