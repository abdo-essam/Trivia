package com.qurio.trivia.data.repository

import com.qurio.trivia.data.database.dao.GameResultDao
import com.qurio.trivia.data.database.dao.UserAchievementDao
import com.qurio.trivia.data.database.dao.UserProgressDao
import com.qurio.trivia.data.database.entity.UserAchievementEntity
import com.qurio.trivia.data.repository.util.AchievementChecker
import com.qurio.trivia.domain.model.Achievement
import com.qurio.trivia.domain.model.UserAchievement
import com.qurio.trivia.domain.repository.AchievementsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementsRepositoryImpl @Inject constructor(
    private val achievementDao: UserAchievementDao,
    private val gameResultDao: GameResultDao,
    private val userProgressDao: UserProgressDao
) : AchievementsRepository {

    override suspend fun initializeAchievements() {
        try {
            val initialAchievements = Achievement.entries.map { achievement ->
                UserAchievementEntity(
                    achievementId = achievement.id,
                    isUnlocked = false,
                    unlockedAt = null,
                    currentProgress = 0
                )
            }
            achievementDao.insertAchievements(initialAchievements)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getAchievements(): List<UserAchievement> {
        return try {
            val storedAchievements = achievementDao.getAllAchievements()

            if (storedAchievements.isEmpty()) {
                initializeAchievements()
                return getAchievements()
            }

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
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun checkAndUnlockAchievements(): List<UserAchievement> {
        return try {
            val lockedAchievements = achievementDao.getLockedAchievements()
            if (lockedAchievements.isEmpty()) return emptyList()

            val gameResults = gameResultDao.getAllGames()
            val userProgress = userProgressDao.getUserProgress()
            val newlyUnlocked = mutableListOf<UserAchievement>()

            lockedAchievements.forEach { storedAchievement ->
                val achievement = Achievement.fromId(storedAchievement.achievementId) ?: return@forEach

                val (progress, shouldUnlock) = AchievementChecker.checkCriteria(
                    achievement,
                    gameResults,
                    userProgress
                )

                if (shouldUnlock) {
                    unlockAchievement(achievement)?.let { newlyUnlocked.add(it) }
                } else {
                    achievementDao.updateProgress(achievement.id, progress)
                }
            }

            if (newlyUnlocked.isNotEmpty()) {
                checkLegendAchievement()?.let { newlyUnlocked.add(it) }
            }

            newlyUnlocked
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getUnlockedCount(): Int {
        return try {
            achievementDao.getUnlockedCount()
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    override suspend fun isAchievementUnlocked(achievementId: String): Boolean {
        return try {
            achievementDao.isAchievementUnlocked(achievementId) ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun unlockAchievement(achievement: Achievement): UserAchievement? {
        return try {
            val now = System.currentTimeMillis()
            val updated = achievementDao.unlockAchievement(achievement.id, now)

            if (updated > 0) {
                UserAchievement(
                    achievement = achievement,
                    isUnlocked = true,
                    unlockedAt = now,
                    currentProgress = achievement.maxProgress
                )
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun checkLegendAchievement(): UserAchievement? {
        val totalAchievements = Achievement.entries.size - 1
        val unlockedCount = getUnlockedCount()
        val legendAchievement = Achievement.LEGEND
        val isLegendUnlocked = isAchievementUnlocked(legendAchievement.id)

        return if (!isLegendUnlocked && unlockedCount >= totalAchievements) {
            unlockAchievement(legendAchievement)
        } else null
    }
}