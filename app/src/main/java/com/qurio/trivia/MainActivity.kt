package com.qurio.trivia

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.data.model.UserProgress
import com.qurio.trivia.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var userProgressDao: UserProgressDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inject dependencies
        (application as QuriοApp).appComponent.inject(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide action bar
        supportActionBar?.hide()

        // Check first launch and setup navigation
        checkFirstLaunchAndNavigate()
    }

    private fun checkFirstLaunchAndNavigate() {
        lifecycleScope.launch {
            val startDestination = withContext(Dispatchers.IO) {
                val isFirstLaunch = sharedPreferences.getBoolean("is_first_launch", true)

                if (isFirstLaunch) {
                    // Initialize user progress
                    val initialProgress = UserProgress()
                    userProgressDao.insertOrUpdateUserProgress(initialProgress)
                    R.id.onboardingFragment
                } else {
                    R.id.homeFragment
                }
            }

            setupNavigation(startDestination)
        }
    }

    private fun setupNavigation(startDestination: Int) {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(startDestination)
        navController.graph = navGraph
    }
}