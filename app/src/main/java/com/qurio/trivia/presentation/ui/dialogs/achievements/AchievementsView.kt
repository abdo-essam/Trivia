package com.qurio.trivia.presentation.ui.dialogs.achievements

import com.qurio.trivia.domain.model.AchievementState
import com.qurio.trivia.presentation.base.BaseView

interface AchievementsView : BaseView {
    fun displayAchievements(achievements: List<AchievementState>)
}