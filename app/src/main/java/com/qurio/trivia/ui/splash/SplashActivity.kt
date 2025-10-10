package com.qurio.trivia.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.qurio.trivia.MainActivity
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.databinding.ActivitySplashBinding
import com.qurio.trivia.utils.PreferenceKeys.IS_FIRST_LAUNCH
import javax.inject.Inject


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var userProgressDao: UserProgressDao

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as QuriοApp).appComponent.inject(this)


        Handler(Looper.getMainLooper()).postDelayed({
            navigateNext()
        }, 1000)
    }

    private fun navigateNext() {
        val isFirstLaunch = sharedPreferences.getBoolean(IS_FIRST_LAUNCH, true)
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(IS_FIRST_LAUNCH, isFirstLaunch)
        }
        startActivity(intent)
        finish()
    }
}
