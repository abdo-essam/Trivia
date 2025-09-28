package com.qurio.trivia.ui.onboarding

import android.content.SharedPreferences
import com.qurio.trivia.base.BasePresenter
import javax.inject.Inject

class OnboardingPresenter @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : BasePresenter<OnboardingView>() {

    fun completeOnboarding() {
        // Mark onboarding as completed
        sharedPreferences.edit()
            .putBoolean("is_first_launch", false)
            .apply()

        view?.navigateToHome()
    }
}