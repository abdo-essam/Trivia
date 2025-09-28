package com.qurio.trivia.ui.achievements

import com.qurio.trivia.R
import com.qurio.trivia.base.BasePresenter
import com.qurio.trivia.data.database.GameResultDao
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.data.model.Achievement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AchievementsPresenter @Inject constructor(
    private val gameResultDao: GameResultDao,
    private val userProgressDao: UserProgressDao
) : BasePresenter<AchievementsView>() {

    fun loadAchievements() {
        CoroutineScope(Dispatchers.IO).launch {
            val gameResults = gameResultDao.getAllGameResults()
            val userProgress = userProgressDao.getUserProgress()

            val achievements = generateAchievements(gameResults, userProgress)

            CoroutineScope(Dispatchers.Main).launch {
                view?.displayAchievements(achievements)
            }
        }
    }

    private fun generateAchievements(gameResults: List<com.qurio.trivia.data.model.GameResult>, userProgress: com.qurio.trivia.data.model.UserProgress?): List<Achievement> {
        val achievements = mutableListOf<Achievement>()

        // First Win
        achievements.add(Achievement(
            id = "first_win",
            title = "First Victory",
            description = "Win your first trivia game",
            iconRes = R.drawable.ic_trophy,
            isUnlocked = gameResults.any { it.stars > 0 },
            progress = if (gameResults.any { it.stars > 0 }) 1 else 0,
            maxProgress = 1
        ))

        // Perfect Score
        achievements.add(Achievement(
            id = "perfect_score",
            title = "Perfect Scholar",
            description = "Get all questions correct in a game",
            iconRes = R.drawable.ic_trophy,
            isUnlocked = gameResults.any { it.stars == 3 },
            progress = gameResults.count { it.stars == 3 },
            maxProgress = 1
        ))

        // Play Streak
        val currentStreak = userProgress?.currentStreak ?: 0
        achievements.add(Achievement(
            id = "streak_5",
            title = "On Fire!",
            description = "Play 5 days in a row",
            iconRes = R.drawable.ic_trophy,
            isUnlocked = currentStreak >= 5,
            progress = minOf(currentStreak, 5),
            maxProgress = 5
        ))

        // Total Games
        achievements.add(Achievement(
            id = "games_10",
            title = "Dedicated Player",
            description = "Complete 10 trivia games",
            iconRes = R.drawable.ic_games,
            isUnlocked = gameResults.size >= 10,
            progress = minOf(gameResults.size, 10),
            maxProgress = 10
        ))

        // Coin Collector
        val totalCoins = userProgress?.totalCoins ?: 0
        achievements.add(Achievement(
            id = "coins_1000",
            title = "Coin Collector",
            description = "Collect 1000 coins",
            iconRes = R.drawable.ic_coin,
            isUnlocked = totalCoins >= 1000,
            progress = minOf(totalCoins, 1000),
            maxProgress = 1000
        ))

        // Category Master
        val categories = gameResults.map { it.category }.distinct()
        achievements.add(Achievement(
            id = "categories_5",
            title = "Renaissance Mind",
            description = "Play in 5 different categories",
            iconRes = R.drawable.ic_trophy,
            isUnlocked = categories.size >= 5,
            progress = minOf(categories.size, 5),
            maxProgress = 5
        ))

        return achievements
    }
}