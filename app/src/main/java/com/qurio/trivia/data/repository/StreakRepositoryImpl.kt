package com.qurio.trivia.data.repository

import com.qurio.trivia.data.database.dao.UserProgressDao
import com.qurio.trivia.domain.repository.StreakRepository
import com.qurio.trivia.utils.StreakHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreakRepositoryImpl @Inject constructor(
    private val userProgressDao: UserProgressDao
) : StreakRepository {

    override suspend fun updateStreakAfterGame() = withContext(Dispatchers.IO) {
        val userProgress = userProgressDao.getUserProgress() ?: return@withContext

        val newStreak = StreakHelper.calculateNewStreak(
            lastPlayedDate = userProgress.lastPlayedDate,
            currentStreak = userProgress.currentStreak,
            streakDays = userProgress.streakDays
        )

        userProgressDao.updateStreak(
            streak = newStreak.streak,
            date = newStreak.date,
            days = newStreak.days
        )
    }

    override suspend fun getCurrentStreakInfo(): StreakRepository.StreakInfo =
        withContext(Dispatchers.IO) {
            val userProgress = userProgressDao.getUserProgress()

            StreakRepository.StreakInfo(
                currentStreak = userProgress?.currentStreak ?: 0,
                lastPlayedDate = userProgress?.lastPlayedDate ?: "",
                activeDays = StreakHelper.parseActiveDays(userProgress?.streakDays ?: "")
            )
        }

    override suspend fun resetStreak() = withContext(Dispatchers.IO) {
        userProgressDao.updateStreak(
            streak = 0,
            date = "",
            days = ""
        )
    }
}