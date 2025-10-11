package com.qurio.trivia.presentation.ui.dialogs.achievements

import android.util.Log
import com.qurio.trivia.presentation.base.BasePresenter
import com.qurio.trivia.data.repository.AchievementsRepository
import javax.inject.Inject

class AchievementsPresenter @Inject constructor(
    private val repository: AchievementsRepository
) : BasePresenter<AchievementsView>() {

    // ========== Load Achievements ==========

    fun loadAchievements() {
        tryToExecute(
            execute = {
                repository.getAchievements()
            },
            onSuccess = { achievements ->
                Log.d(TAG, "Loaded ${achievements.size} achievements")
                val unlockedCount = achievements.count { it.isUnlocked }
                Log.d(TAG, "Unlocked: $unlockedCount/${achievements.size}")
                withView { displayAchievements(achievements) }
            },
            onError = { error ->
                Log.e(TAG, "Error loading achievements", error)
                withView { showError("Failed to load achievements") }
            },
            showLoading = false
        )
    }

    companion object {
        private const val TAG = "AchievementsPresenter"
    }
}