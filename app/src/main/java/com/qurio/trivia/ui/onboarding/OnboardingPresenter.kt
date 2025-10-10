package com.qurio.trivia.ui.onboarding

import android.content.SharedPreferences
import android.util.Log
import com.qurio.trivia.base.BasePresenter
import com.qurio.trivia.utils.PreferenceKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.core.content.edit

class OnboardingPresenter @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : BasePresenter<OnboardingView>() {

    fun completeOnboarding() {
        CoroutineScope(Dispatchers.IO).launch {

            // Mark onboarding as completed
            sharedPreferences.edit {
                putBoolean(PreferenceKeys.IS_FIRST_LAUNCH, false)
                Log.d("Abdoooo3", "Onboarding completed, IS_FIRST_LAUNCH set to false")
            }

            withContext(Dispatchers.Main) {
                view?.navigateToHome()
            }
        }
    }
}