package com.qurio.trivia.data.repository

import android.util.Log
import com.qurio.trivia.data.database.dao.GameResultDao
import com.qurio.trivia.data.database.dao.UserProgressDao
import com.qurio.trivia.data.database.entity.GameResultEntity
import com.qurio.trivia.data.database.entity.UserProgressEntity
import com.qurio.trivia.domain.model.Achievement
import com.qurio.trivia.domain.model.AchievementState
import com.qurio.trivia.domain.repository.AchievementsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementsRepositoryImpl @Inject constructor(
    private val gameResultDao: GameResultDao,
    private val userProgressDao: UserProgressDao
) : AchievementsRepository {

    companion object {
        private const val TAG = "AchievementsRepository"
        private const val SPEED_THRESHOLD_MS = 60000L // 1 minute
    }

    override suspend fun getAchievements(): List<AchievementState> = withContext(Dispatchers.IO) {
        try {
            val gameResults = gameResultDao.getAllGames()
            val userProgress = userProgressDao.getUserProgress()

            Achievement.entries.map { achievement ->
                val (progress, isUnlocked) = calculateProgress(
                    achievement,
                    gameResults,
                    userProgress
                )

                AchievementState(
                    achievement = achievement,
                    progress = progress,
                    isUnlocked = isUnlocked
                )
            }
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

    override suspend fun isAchievementUnlocked(achievementId: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                getAchievements().find { it.id == achievementId }?.isUnlocked ?: false
            } catch (e: Exception) {
                Log.e(TAG, "Error checking unlock status", e)
                false
            }
        }

    private fun calculateProgress(
        achievement: Achievement,
        gameResults: List<GameResultEntity>,
        userProgress: UserProgressEntity?
    ): Pair<Int, Boolean> {
        return when (achievement) {
            Achievement.QUIZ_ROOKIE -> {
                val progress = gameResults.size.coerceAtMost(1)
                progress to (gameResults.isNotEmpty())
            }

            Achievement.STREAK_STARTER -> {
                val streak = userProgress?.currentStreak ?: 0
                val progress = streak.coerceAtMost(achievement.maxProgress)
                progress to (streak >= achievement.maxProgress)
            }

            Achievement.LUCKY_GUESS -> {
                val maxConsecutive = calculateMaxConsecutiveCorrect(gameResults)
                val progress = maxConsecutive.coerceAtMost(achievement.maxProgress)
                progress to (maxConsecutive >= achievement.maxProgress)
            }

            Achievement.EXPLORER -> {
                val categories = gameResults.map { it.category }.distinct().size
                val progress = categories.coerceAtMost(achievement.maxProgress)
                progress to (categories >= achievement.maxProgress)
            }

            Achievement.TRIVIA_CHAMP -> {
                val progress = gameResults.size.coerceAtMost(achievement.maxProgress)
                progress to (gameResults.size >= achievement.maxProgress)
            }

            Achievement.COLLECTOR -> {
                val totalCoins = userProgress?.totalCoins ?: 0
                val progress = totalCoins.coerceAtMost(achievement.maxProgress)
                progress to (totalCoins >= achievement.maxProgress)
            }

            Achievement.LEGEND -> {
                val unlockedCount = 0 // Calculate separately to avoid recursion
                val progress = unlockedCount.coerceAtMost(achievement.maxProgress)
                progress to (unlockedCount >= achievement.maxProgress)
            }

            Achievement.UNTOUCHABLE -> {
                val perfectScores = gameResults.count { it.stars == 3 }
                val progress = perfectScores.coerceAtMost(achievement.maxProgress)
                progress to (perfectScores >= achievement.maxProgress)
            }

            Achievement.QUICK_THINKER -> {
                val fastGames = gameResults.count { it.timeTaken < SPEED_THRESHOLD_MS }
                val progress = fastGames.coerceAtMost(1)
                progress to (fastGames > 0)
            }

            Achievement.COLLECTOR_MASTER -> {
                val totalCoins = userProgress?.totalCoins ?: 0
                val progress = totalCoins.coerceAtMost(achievement.maxProgress)
                progress to (totalCoins >= achievement.maxProgress)
            }

            Achievement.LUCKY_GUESS_MASTER -> {
                val maxConsecutive = calculateMaxConsecutiveCorrect(gameResults)
                val progress = maxConsecutive.coerceAtMost(achievement.maxProgress)
                progress to (maxConsecutive >= achievement.maxProgress)
            }
        }
    }

    private fun calculateMaxConsecutiveCorrect(
        gameResults: List<GameResultEntity>
    ): Int {
        return gameResults
            .sortedBy { it.timestamp }
            .fold(0 to 0) { (current, max), game ->
                val newCurrent = if (game.correctAnswers == game.totalQuestions) {
                    current + game.correctAnswers
                } else {
                    0
                }
                newCurrent to maxOf(max, newCurrent)
            }
            .second
    }
}