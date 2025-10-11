package com.qurio.trivia.presentation.ui.onboarding

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.qurio.trivia.presentation.base.BasePresenter
import com.qurio.trivia.utils.PreferenceKeys
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class OnboardingPresenter @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : BasePresenter<OnboardingView>() {

    fun completeOnboarding() {
        tryToExecute(
            execute = {
                // Mark onboarding as completed
                sharedPreferences.edit {
                    putBoolean(PreferenceKeys.IS_FIRST_LAUNCH, false)
                }
                Log.d(TAG, "Onboarding completed successfully")
            },
            onSuccess = {
                Log.d(TAG, "Navigating to home screen")
                withView { navigateToHome() }
            },
            onError = { error ->
                Log.e(TAG, "Error completing onboarding", error)
                withView {
                    showError("Failed to complete onboarding")
                    // Still navigate even if preferences fail
                    navigateToHome()
                }
            },
            showLoading = false,
            dispatcher = Dispatchers.IO
        )
    }

    companion object {
        private const val TAG = "OnboardingPresenter"
    }
}