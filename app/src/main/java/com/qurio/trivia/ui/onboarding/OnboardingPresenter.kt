package com.qurio.trivia.ui.onboarding

import android.content.SharedPreferences
import com.qurio.trivia.base.BasePresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.content.edit

class OnboardingPresenter @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : BasePresenter<OnboardingView>() {

    fun completeOnboarding() {
        CoroutineScope(Dispatchers.IO).launch {
            // Mark onboarding as completed
            sharedPreferences.edit {
                putBoolean("is_first_launch", false)
            }

            CoroutineScope(Dispatchers.Main).launch {
                view?.navigateToHome()
            }
        }
    }
}