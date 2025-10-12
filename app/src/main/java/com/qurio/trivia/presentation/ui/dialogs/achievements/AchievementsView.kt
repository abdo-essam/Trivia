package com.qurio.trivia.presentation.ui.dialogs.achievements

import com.qurio.trivia.domain.model.Achievement
import com.qurio.trivia.presentation.base.BaseView

/**
 * View contract for AchievementsDialog
 */
interface AchievementsView : BaseView {
    fun displayAchievements(achievements: List<Achievement>)
}