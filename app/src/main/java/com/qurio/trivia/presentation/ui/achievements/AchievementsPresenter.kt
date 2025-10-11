package com.qurio.trivia.presentation.ui.achievements

import com.qurio.trivia.R
import com.qurio.trivia.presentation.base.BasePresenter
import com.qurio.trivia.data.database.GameResultDao
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.data.model.Achievement
import com.qurio.trivia.data.model.GameResult
import com.qurio.trivia.data.model.UserProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AchievementsPresenter @Inject constructor(
    private val gameResultDao: GameResultDao,
    private val userProgressDao: UserProgressDao
) : BasePresenter<AchievementsView>() {

    fun loadAchievements() {
        CoroutineScope(Dispatchers.IO).launch {
            val gameResults = gameResultDao.getAllGames()
            val userProgress = userProgressDao.getUserProgress()

            val achievements = generateAchievements(gameResults, userProgress)

            withContext(Dispatchers.Main) {
                view?.displayAchievements(achievements)
            }
        }
    }

    private fun generateAchievements(
        gameResults: List<GameResult>,
        userProgress: UserProgress?
    ): List<Achievement> {
        val achievements = mutableListOf<Achievement>()

        // Quiz Rookie
        achievements.add(Achievement(
            id = "quiz_rookie",
            title = "Quiz Rookie",
            description = "Complete your first quiz",
            howToGet = "Play and complete any quiz game.",
            iconRes = R.drawable.ic_trophy, // Use existing icon
            iconLockedRes = R.drawable.ic_trophy, // Or same with alpha
            isUnlocked = gameResults.isNotEmpty(),
            progress = if (gameResults.isNotEmpty()) 1 else 0,
            maxProgress = 1
        ))

        // Continue with other achievements...

        return achievements
    }
}