package com.qurio.trivia.presentation.ui.dialogs.achievements

import com.qurio.trivia.domain.repository.AchievementsRepository
import com.qurio.trivia.presentation.base.BasePresenter
import javax.inject.Inject

class AchievementsPresenter @Inject constructor(
    private val achievementsRepository: AchievementsRepository
) : BasePresenter<AchievementsView>() {
    fun loadAchievements() {
        tryToExecute(
            execute = {
                achievementsRepository.getAchievements()
            },
            onSuccess = { achievements ->
                withView { displayAchievements(achievements) }
            },
            onError = { error ->
                withView { showError("Failed to load achievements") }
            },
            showLoading = true
        )
    }
}