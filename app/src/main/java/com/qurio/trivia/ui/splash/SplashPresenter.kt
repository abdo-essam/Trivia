package com.qurio.trivia.ui.splash

import android.content.SharedPreferences
import com.qurio.trivia.base.BasePresenter
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.data.model.UserProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashPresenter @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val userProgressDao: UserProgressDao
) : BasePresenter<SplashView>() {

    fun checkFirstLaunch() {
        CoroutineScope(Dispatchers.IO).launch {
            val isFirstLaunch = sharedPreferences.getBoolean("is_first_launch", true)

            if (isFirstLaunch) {
                // Initialize user progress
                val initialProgress = UserProgress()
                userProgressDao.insertOrUpdateUserProgress(initialProgress)

                CoroutineScope(Dispatchers.Main).launch {
                    view?.navigateToOnboarding()
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    view?.navigateToHome()
                }
            }
        }
    }
}