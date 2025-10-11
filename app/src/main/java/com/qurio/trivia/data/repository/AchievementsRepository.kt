package com.qurio.trivia.data.repository

import com.qurio.trivia.R
import com.qurio.trivia.data.database.GameResultDao
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.data.model.Achievement
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementsRepository @Inject constructor(
    private val gameResultDao: GameResultDao,
    private val userProgressDao: UserProgressDao
) {

    suspend fun getAchievements(): List<Achievement> {
        val gameResults = gameResultDao.getAllGames()
        val userProgress = userProgressDao.getUserProgress()

        return buildList {
            // Quiz Rookie
            add(Achievement(
                id = "quiz_rookie",
                title = "Quiz Rookie",
                description = "Complete your first quiz",
                howToGet = "Play and complete any quiz game.",
                iconRes = R.drawable.ic_achievement_rookie,
                iconLockedRes = R.drawable.ic_lock,
                isUnlocked = gameResults.isNotEmpty(),
                progress = minOf(gameResults.size, 1),
                maxProgress = 1
            ))

            // Quiz Master
            add(Achievement(
                id = "quiz_master",
                title = "Quiz Master",
                description = "Complete 10 quizzes",
                howToGet = "Play and complete 10 quiz games.",
                iconRes = R.drawable.ic_achievement_master,
                iconLockedRes = R.drawable.ic_lock,
                isUnlocked = gameResults.size >= 10,
                progress = minOf(gameResults.size, 10),
                maxProgress = 10
            ))

            // Perfect Score
            val perfectScores = gameResults.count { it.stars == 3 }
            add(Achievement(
                id = "perfect_score",
                title = "Perfect Score",
                description = "Get 3 stars in a quiz",
                howToGet = "Answer all questions correctly without skipping any.",
                iconRes = R.drawable.ic_achievement_perfect,
                iconLockedRes = R.drawable.ic_lock,
                isUnlocked = perfectScores > 0,
                progress = minOf(perfectScores, 1),
                maxProgress = 1
            ))

            // Coin Collector
            val totalCoins = userProgress?.totalCoins ?: 0
            add(Achievement(
                id = "coin_collector",
                title = "Coin Collector",
                description = "Earn 1000 coins",
                howToGet = "Play quizzes and accumulate 1000 coins.",
                iconRes = R.drawable.ic_achievement_coins,
                iconLockedRes = R.drawable.ic_lock,
                isUnlocked = totalCoins >= 1000,
                progress = minOf(totalCoins, 1000),
                maxProgress = 1000
            ))

            // Streak Master
            val currentStreak = userProgress?.currentStreak ?: 0
            add(Achievement(
                id = "streak_master",
                title = "Streak Master",
                description = "Maintain a 7-day streak",
                howToGet = "Play at least one quiz every day for 7 days.",
                iconRes = R.drawable.ic_achievement_streak,
                iconLockedRes = R.drawable.ic_lock,
                isUnlocked = currentStreak >= 7,
                progress = minOf(currentStreak, 7),
                maxProgress = 7
            ))

            // Category Explorer
            val uniqueCategories = gameResults.map { it.category }.distinct().size
            add(Achievement(
                id = "category_explorer",
                title = "Category Explorer",
                description = "Play 5 different categories",
                howToGet = "Complete quizzes from 5 different categories.",
                iconRes = R.drawable.ic_achievement_explorer,
                iconLockedRes = R.drawable.ic_lock,
                isUnlocked = uniqueCategories >= 5,
                progress = minOf(uniqueCategories, 5),
                maxProgress = 5
            ))

            // Speed Demon
            val fastGames = gameResults.count { it.timeTaken < 60000 } // Under 1 minute
            add(Achievement(
                id = "speed_demon",
                title = "Speed Demon",
                description = "Complete a quiz in under 1 minute",
                howToGet = "Answer all questions quickly and correctly.",
                iconRes = R.drawable.ic_achievement_speed,
                iconLockedRes = R.drawable.ic_lock,
                isUnlocked = fastGames > 0,
                progress = minOf(fastGames, 1),
                maxProgress = 1
            ))

            // Knowledge Hoarder
            val totalCorrect = gameResults.sumOf { it.correctAnswers }
            add(Achievement(
                id = "knowledge_hoarder",
                title = "Knowledge Hoarder",
                description = "Answer 100 questions correctly",
                howToGet = "Keep playing and answering correctly.",
                iconRes = R.drawable.ic_achievement_knowledge,
                iconLockedRes = R.drawable.ic_lock,
                isUnlocked = totalCorrect >= 100,
                progress = minOf(totalCorrect, 100),
                maxProgress = 100
            ))
        }
    }
}