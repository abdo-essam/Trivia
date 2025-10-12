package com.qurio.trivia.presentation.ui.onboarding

import android.content.SharedPreferences
import androidx.core.content.edit
import com.qurio.trivia.data.database.DatabaseSeeder
import com.qurio.trivia.presentation.base.BasePresenter
import com.qurio.trivia.utils.PreferenceKeys
import javax.inject.Inject

class OnboardingPresenter @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val databaseSeeder: DatabaseSeeder
) : BasePresenter<OnboardingView>() {


    fun completeOnboarding() {
        tryToExecute(
            execute = {
                // Step 1: Initialize database
                databaseSeeder.seedDatabase()

                // Step 2: Mark onboarding as completed
                sharedPreferences.edit {
                    putBoolean(PreferenceKeys.IS_FIRST_LAUNCH, false)
                }
            },
            onSuccess = {
                withView { navigateToHome() }
            },
            onError = { error ->
                withView {
                    showError("Failed to complete onboarding")
                }
            },
            showLoading = true
        )
    }
}