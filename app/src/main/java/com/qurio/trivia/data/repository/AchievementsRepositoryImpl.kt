package com.qurio.trivia.data.repository

import android.util.Log
import com.qurio.trivia.data.database.dao.GameResultDao
import com.qurio.trivia.data.database.dao.UserAchievementDao
import com.qurio.trivia.data.database.dao.UserProgressDao
import com.qurio.trivia.data.database.entity.GameResultEntity
import com.qurio.trivia.data.database.entity.UserAchievementEntity
import com.qurio.trivia.data.database.entity.UserProgressEntity
import com.qurio.trivia.domain.model.Achievement
import com.qurio.trivia.domain.model.UserAchievement
import com.qurio.trivia.domain.repository.AchievementsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementsRepositoryImpl @Inject constructor(
    private val achievementDao: UserAchievementDao,
    private val gameResultDao: GameResultDao,
    private val userProgressDao: UserProgressDao
) : AchievementsRepository {

    companion object {
        private const val TAG = "AchievementsRepository"
        private const val SPEED_THRESHOLD_MS = 60000L
    }

    override suspend fun initializeAchievements() {
        try {
            // Insert all achievements as locked
            val initialAchievements = Achievement.entries.map { achievement ->
                UserAchievementEntity(
                    achievementId = achievement.id,
                    isUnlocked = false,
                    unlockedAt = null,
                    currentProgress = 0
                )
            }
            achievementDao.insertAchievements(initialAchievements)
            Log.d(TAG, "Initialized ${initialAchievements.size} achievements")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing achievements", e)
        }
    }

    override suspend fun getAchievements(): List<UserAchievement> = withContext(Dispatchers.IO) {
        try {
            val storedAchievements = achievementDao.getAllAchievements()

            // If no achievements in DB, initialize them
            if (storedAchievements.isEmpty()) {
                initializeAchievements()
                return@withContext getAchievements() // Recursive call after initialization
            }

            // Map stored achievements to domain model
            Achievement.entries.mapNotNull { achievement ->
                val stored = storedAchievements.find { it.achievementId == achievement.id }

                stored?.let {
                    UserAchievement(
                        achievement = achievement,
                        isUnlocked = it.isUnlocked,
                        unlockedAt = it.unlockedAt,
                        currentProgress = if (it.isUnlocked) achievement.maxProgress else it.currentProgress
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading achievements", e)
            emptyList()
        }
    }

    override suspend fun checkAndUnlockAchievements(): List<UserAchievement> =
        withContext(Dispatchers.IO) {
            try {
                // Only get locked achievements
                val lockedAchievements = achievementDao.getLockedAchievements()

                if (lockedAchievements.isEmpty()) {
                    Log.d(TAG, "All achievements already unlocked")
                    return@withContext emptyList()
                }

                val gameResults = gameResultDao.getAllGames()
                val userProgress = userProgressDao.getUserProgress()
                val newlyUnlocked = mutableListOf<UserAchievement>()

                // Check only locked achievements
                lockedAchievements.forEach { storedAchievement ->
                    val achievement = Achievement.fromId(storedAchievement.achievementId) ?: return@forEach

                    val (progress, shouldUnlock) = checkAchievementCriteria(
                        achievement,
                        gameResults,
                        userProgress
                    )

                    if (shouldUnlock) {
                        // Unlock the achievement (only if it was locked)
                        val now = System.currentTimeMillis()
                        val updated = achievementDao.unlockAchievement(achievement.id, now)

                        if (updated > 0) {
                            val userAchievement = UserAchievement(
                                achievement = achievement,
                                isUnlocked = true,
                                unlockedAt = now,
                                currentProgress = achievement.maxProgress
                            )
                            newlyUnlocked.add(userAchievement)
                            Log.d(TAG, "🏆 Achievement unlocked: ${achievement.title}")
                        }
                    } else {
                        // Update progress for locked achievement
                        achievementDao.updateProgress(achievement.id, progress)
                    }
                }

                // Check Legend achievement after unlocking others
                if (newlyUnlocked.isNotEmpty()) {
                    checkLegendAchievement()?.let { newlyUnlocked.add(it) }
                }

                newlyUnlocked
            } catch (e: Exception) {
                Log.e(TAG, "Error checking achievements", e)
                emptyList()
            }
        }

    override suspend fun getUnlockedCount(): Int = withContext(Dispatchers.IO) {
        try {
            achievementDao.getUnlockedCount()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting unlocked count", e)
            0
        }
    }

    override suspend fun isAchievementUnlocked(achievementId: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                achievementDao.isAchievementUnlocked(achievementId) ?: false
            } catch (e: Exception) {
                Log.e(TAG, "Error checking unlock status", e)
                false
            }
        }

    private suspend fun checkLegendAchievement(): UserAchievement? {
        val totalAchievements = Achievement.entries.size - 1 // Exclude Legend itself
        val unlockedCount = achievementDao.getUnlockedCount()

        val legendAchievement = Achievement.LEGEND
        val isLegendUnlocked = achievementDao.isAchievementUnlocked(legendAchievement.id)

        if (isLegendUnlocked != true && unlockedCount >= totalAchievements) {
            val now = System.currentTimeMillis()
            achievementDao.unlockAchievement(legendAchievement.id, now)

            Log.d(TAG, "🏆 LEGEND Achievement unlocked!")

            return UserAchievement(
                achievement = legendAchievement,
                isUnlocked = true,
                unlockedAt = now,
                currentProgress = legendAchievement.maxProgress
            )
        }

        return null
    }

    private fun checkAchievementCriteria(
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

            Achievement.LEGEND -> 0 to false // Handled separately

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
        if (gameResults.isEmpty()) return 0

        var maxConsecutive = 0
        var currentConsecutive = 0

        gameResults.sortedBy { it.timestamp }.forEach { game ->
            if (game.correctAnswers == game.totalQuestions) {
                currentConsecutive += game.correctAnswers
                maxConsecutive = maxOf(maxConsecutive, currentConsecutive)
            } else {
                currentConsecutive = 0
            }
        }

        return maxConsecutive
    }
}