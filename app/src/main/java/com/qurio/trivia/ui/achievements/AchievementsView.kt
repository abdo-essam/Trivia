package com.qurio.trivia.ui.achievements

import com.qurio.trivia.base.BaseView
import com.qurio.trivia.data.model.Achievement

interface AchievementsView : BaseView{
    fun displayAchievements(achievements: List<Achievement>)
}