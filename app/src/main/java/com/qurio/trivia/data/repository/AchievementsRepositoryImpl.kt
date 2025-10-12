package com.qurio.trivia.data.repository

import android.util.Log
import com.qurio.trivia.R
import com.qurio.trivia.data.database.dao.GameResultDao
import com.qurio.trivia.data.database.dao.UserProgressDao
import com.qurio.trivia.data.database.entity.GameResultEntity
import com.qurio.trivia.data.database.entity.UserProgressEntity
import com.qurio.trivia.domain.model.Achievement
import com.qurio.trivia.domain.repository.AchievementsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AchievementsRepository
 * Calculates achievement progress based on user stats and game history
 */
@Singleton
class AchievementsRepositoryImpl @Inject constructor(
    private val gameResultDao: GameResultDao,
    private val userProgressDao: UserProgressDao
) : AchievementsRepository {

    companion object {
        private const val TAG = "AchievementsRepositoryImpl"

        // Achievement thresholds
        private const val QUIZ_MASTER_THRESHOLD = 10
        private const val COIN_COLLECTOR_THRESHOLD = 1000
        private const val STREAK_MASTER_THRESHOLD = 7
        private const val CATEGORY_EXPLORER_THRESHOLD = 5
        private const val SPEED_THRESHOLD_MS = 60000L // 1 minute
        private const val KNOWLEDGE_HOARDER_THRESHOLD = 100
    }

    override suspend fun getAchievements(): List<Achievement> = withContext(Dispatchers.IO) {
        try {
            val gameResults = gameResultDao.getAllGames()
            val userProgress = userProgressDao.getUserProgress()

            buildAchievementsList(gameResults, userProgress)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading achievements", e)
            emptyList()
        }
    }

    override suspend fun getUnlockedCount(): Int = withContext(Dispatchers.IO) {
        try {
            getAchievements().count { it.isUnlocked }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting unlocked count", e)
            0
        }
    }

    override suspend fun isAchievementUnlocked(achievementId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            getAchievements().find { it.id == achievementId }?.isUnlocked ?: false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking achievement unlock status", e)
            false
        }
    }

    // ========== Private Helper Methods ==========

    private fun buildAchievementsList(
        gameResults: List<GameResultEntity>,
        userProgress: UserProgressEntity?
    ): List<Achievement> {
        return listOf(
            createQuizRookieAchievement(gameResults),
            createQuizMasterAchievement(gameResults),
            createPerfectScoreAchievement(gameResults),
            createCoinCollectorAchievement(userProgress),
            createStreakMasterAchievement(userProgress),
            createCategoryExplorerAchievement(gameResults),
            createSpeedDemonAchievement(gameResults),
            createKnowledgeHoarderAchievement(gameResults)
        )
    }

    private fun createQuizRookieAchievement(
        gameResults: List<GameResultEntity>
    ): Achievement {
        val progress = minOf(gameResults.size, 1)
        return Achievement(
            id = Achievement.QUIZ_ROOKIE,
            title = "Quiz Rookie",
            description = "Complete your first quiz",
            howToGet = "Play and complete any quiz game.",
            iconRes = R.drawable.ic_achievement_rookie,
            iconLockedRes = R.drawable.ic_lock,
            isUnlocked = gameResults.isNotEmpty(),
            progress = progress,
            maxProgress = 1
        )
    }

    private fun createQuizMasterAchievement(
        gameResults: List<GameResultEntity>
    ): Achievement {
        val progress = minOf(gameResults.size, QUIZ_MASTER_THRESHOLD)
        return Achievement(
            id = Achievement.QUIZ_MASTER,
            title = "Quiz Master",
            description = "Complete 10 quizzes",
            howToGet = "Play and complete 10 quiz games.",
            iconRes = R.drawable.ic_achievement_master,
            iconLockedRes = R.drawable.ic_lock,
            isUnlocked = gameResults.size >= QUIZ_MASTER_THRESHOLD,
            progress = progress,
            maxProgress = QUIZ_MASTER_THRESHOLD
        )
    }

    private fun createPerfectScoreAchievement(
        gameResults: List<GameResultEntity>
    ): Achievement {
        val perfectScores = gameResults.count { it.stars == 3 }
        val progress = minOf(perfectScores, 1)
        return Achievement(
            id = Achievement.PERFECT_SCORE,
            title = "Perfect Score",
            description = "Get 3 stars in a quiz",
            howToGet = "Answer all questions correctly without skipping any.",
            iconRes = R.drawable.ic_achievement_perfect,
            iconLockedRes = R.drawable.ic_lock,
            isUnlocked = perfectScores > 0,
            progress = progress,
            maxProgress = 1
        )
    }

    private fun createCoinCollectorAchievement(
        userProgress: UserProgressEntity?
    ): Achievement {
        val totalCoins = userProgress?.totalCoins ?: 0
        val progress = minOf(totalCoins, COIN_COLLECTOR_THRESHOLD)
        return Achievement(
            id = Achievement.COIN_COLLECTOR,
            title = "Coin Collector",
            description = "Earn 1000 coins",
            howToGet = "Play quizzes and accumulate 1000 coins.",
            iconRes = R.drawable.ic_achievement_coins,
            iconLockedRes = R.drawable.ic_lock,
            isUnlocked = totalCoins >= COIN_COLLECTOR_THRESHOLD,
            progress = progress,
            maxProgress = COIN_COLLECTOR_THRESHOLD
        )
    }

    private fun createStreakMasterAchievement(
        userProgress: UserProgressEntity?
    ): Achievement {
        val currentStreak = userProgress?.currentStreak ?: 0
        val progress = minOf(currentStreak, STREAK_MASTER_THRESHOLD)
        return Achievement(
            id = Achievement.STREAK_MASTER,
            title = "Streak Master",
            description = "Maintain a 7-day streak",
            howToGet = "Play at least one quiz every day for 7 days.",
            iconRes = R.drawable.ic_achievement_streak,
            iconLockedRes = R.drawable.ic_lock,
            isUnlocked = currentStreak >= STREAK_MASTER_THRESHOLD,
            progress = progress,
            maxProgress = STREAK_MASTER_THRESHOLD
        )
    }

    private fun createCategoryExplorerAchievement(
        gameResults: List<GameResultEntity>
    ): Achievement {
        val uniqueCategories = gameResults.map { it.category }.distinct().size
        val progress = minOf(uniqueCategories, CATEGORY_EXPLORER_THRESHOLD)
        return Achievement(
            id = Achievement.CATEGORY_EXPLORER,
            title = "Category Explorer",
            description = "Play 5 different categories",
            howToGet = "Complete quizzes from 5 different categories.",
            iconRes = R.drawable.ic_achievement_explorer,
            iconLockedRes = R.drawable.ic_lock,
            isUnlocked = uniqueCategories >= CATEGORY_EXPLORER_THRESHOLD,
            progress = progress,
            maxProgress = CATEGORY_EXPLORER_THRESHOLD
        )
    }

    private fun createSpeedDemonAchievement(
        gameResults: List<GameResultEntity>
    ): Achievement {
        val fastGames = gameResults.count { it.timeTaken < SPEED_THRESHOLD_MS }
        val progress = minOf(fastGames, 1)
        return Achievement(
            id = Achievement.SPEED_DEMON,
            title = "Speed Demon",
            description = "Complete a quiz in under 1 minute",
            howToGet = "Answer all questions quickly and correctly.",
            iconRes = R.drawable.ic_achievement_speed,
            iconLockedRes = R.drawable.ic_lock,
            isUnlocked = fastGames > 0,
            progress = progress,
            maxProgress = 1
        )
    }

    private fun createKnowledgeHoarderAchievement(
        gameResults: List<GameResultEntity>
    ): Achievement {
        val totalCorrect = gameResults.sumOf { it.correctAnswers }
        val progress = minOf(totalCorrect, KNOWLEDGE_HOARDER_THRESHOLD)
        return Achievement(
            id = Achievement.KNOWLEDGE_HOARDER,
            title = "Knowledge Hoarder",
            description = "Answer 100 questions correctly",
            howToGet = "Keep playing and answering correctly.",
            iconRes = R.drawable.ic_achievement_knowledge,
            iconLockedRes = R.drawable.ic_lock,
            isUnlocked = totalCorrect >= KNOWLEDGE_HOARDER_THRESHOLD,
            progress = progress,
            maxProgress = KNOWLEDGE_HOARDER_THRESHOLD
        )
    }
}