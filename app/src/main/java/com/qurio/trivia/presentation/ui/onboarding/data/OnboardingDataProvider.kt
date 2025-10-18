package com.qurio.trivia.presentation.ui.onboarding.data

import android.content.Context
import com.qurio.trivia.R
import com.qurio.trivia.presentation.ui.onboarding.model.OnboardingItem
import javax.inject.Inject

class OnboardingDataProvider @Inject constructor() {

    fun getOnboardingItems(context: Context): List<OnboardingItem> {
        return listOf(
            OnboardingItem(
                imageRes = R.drawable.onboarding_1,
                title = context.getString(R.string.onboarding_welcome_title),
                description = context.getString(R.string.onboarding_welcome_description)
            ),
            OnboardingItem(
                imageRes = R.drawable.onboarding_2,
                title = context.getString(R.string.onboarding_character_title),
                description = context.getString(R.string.onboarding_character_description)
            ),
            OnboardingItem(
                imageRes = R.drawable.onboarding_3,
                title = context.getString(R.string.onboarding_challenge_title),
                description = context.getString(R.string.onboarding_challenge_description)
            ),
            OnboardingItem(
                imageRes = R.drawable.onboarding_4,
                title = context.getString(R.string.onboarding_collect_title),
                description = context.getString(R.string.onboarding_collect_description)
            )
        )
    }

    companion object {
        const val TOTAL_PAGES = 4
    }
}