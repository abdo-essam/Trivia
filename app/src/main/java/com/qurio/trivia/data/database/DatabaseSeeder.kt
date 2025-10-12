package com.qurio.trivia.data.database

import android.util.Log
import com.qurio.trivia.data.database.dao.UserProgressDao
import com.qurio.trivia.data.database.entity.UserProgressEntity
import com.qurio.trivia.domain.model.Character
import com.qurio.trivia.utils.StreakHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val userProgressDao: UserProgressDao
) {
    companion object {
        private const val TAG = "DatabaseSeeder"
    }

    suspend fun seedDatabase() = withContext(Dispatchers.IO) {
        try {
            seedUserProgress()
            Log.d(TAG, "✓ Database seeded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "✗ Error seeding database", e)
        }
    }

    private suspend fun seedUserProgress() {
        if (userProgressDao.getUserProgress() == null) {
            userProgressDao.insertOrUpdateUserProgress(
                UserProgressEntity(
                    id = 1,
                    lives = 4,
                    totalCoins = 50000,
                    awards = 0,
                    selectedCharacter = Character.default().characterName,
                    soundEnabled = true,
                    musicEnabled = true,
                    currentStreak = 3, // Test with 3 day streak
                    lastPlayedDate = StreakHelper.getCurrentDate(), // Today
                    streakDays = "0,1,2" // Sunday, Monday, Tuesday active
                )
            )
            Log.d(TAG, "✓ User progress seeded")
        }
    }
}