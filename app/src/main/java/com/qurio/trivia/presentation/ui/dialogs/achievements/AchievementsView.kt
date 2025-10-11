package com.qurio.trivia.presentation.ui.dialogs.achievements

import com.qurio.trivia.presentation.base.BaseView
import com.qurio.trivia.data.model.Achievement

interface AchievementsView : BaseView {
    fun displayAchievements(achievements: List<Achievement>)
}