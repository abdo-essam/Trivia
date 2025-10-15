package com.qurio.trivia.presentation.ui.dialogs.achievements

import android.util.Log
import com.qurio.trivia.domain.repository.AchievementsRepository
import com.qurio.trivia.presentation.base.BasePresenter
import javax.inject.Inject

class AchievementsPresenter @Inject constructor(
    private val achievementsRepository: AchievementsRepository
) : BasePresenter<AchievementsView>() {

    companion object {
        private const val TAG = "AchievementsPresenter"
    }

    fun loadAchievements() {
        tryToExecute(
            execute = {
                achievementsRepository.getAchievements()
            },
            onSuccess = { achievements ->
                val unlockedCount = achievements.count { it.isUnlocked }
                Log.d(TAG, "✓ Loaded ${achievements.size} achievements ($unlockedCount unlocked)")
                withView { displayAchievements(achievements) }
            },
            onError = { error ->
                Log.e(TAG, "✗ Failed to load achievements", error)
                withView { showError("Failed to load achievements") }
            },
            showLoading = true
        )
    }
}