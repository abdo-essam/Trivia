package com.qurio.trivia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.qurio.trivia.databinding.ActivityMainBinding
import com.qurio.trivia.utils.PreferenceKeys.IS_FIRST_LAUNCH
import com.qurio.trivia.utils.PreferenceKeys.PREFS_NAME
import com.qurio.trivia.utils.sound.SoundManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val soundManager: SoundManager by lazy {
        (application as QuriοApp).soundManager
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        installSplashScreen()
        setupNavigation()

        soundManager.playBackgroundMusic(R.raw.app_theme_1)
    }

    private fun setupNavigation() {
        val sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val isFirstLaunch = sharedPrefs.getBoolean(IS_FIRST_LAUNCH, true)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        val startDestination = if (isFirstLaunch) {
            R.id.onboardingFragment
        } else {
            R.id.homeFragment
        }

        navGraph.setStartDestination(startDestination)
        navController.graph = navGraph
    }


    override fun onPause() {
        super.onPause()
        soundManager.pauseMusic()
    }

    override fun onResume() {
        super.onResume()
        soundManager.resumeMusic()
    }

    override fun onDestroy() {
        soundManager.stopMusic()
        super.onDestroy()
    }

}
