package com.qurio.trivia.data.database

import com.qurio.trivia.data.model.UserProgress
import com.qurio.trivia.data.provider.DataProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val userProgressDao: UserProgressDao,
    private val gameResultDao: GameResultDao
) {

    fun seedDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            // Check if database is already seeded
            val existingProgress = userProgressDao.getUserProgress()

            if (existingProgress == null) {
                // Seed user progress
                seedUserProgress()
            }

            // Check if game results exist
            val existingGames = gameResultDao.getAllGames()
            if (existingGames.isEmpty()) {
                // Seed game results
                seedGameResults()
            }
        }
    }

    private suspend fun seedUserProgress() {
        val defaultUserProgress = UserProgress(
            id = 1,
            lives = 4,
            totalCoins = 5400,
            awards = 2,
            selectedCharacter = "rika",
            soundEnabled = true,
            musicEnabled = true,
            currentStreak = 3,
            lastPlayedDate = "2025-08-03",
            streakDays = "0,1,2" // Sunday, Monday, Tuesday
        )
        userProgressDao.insertOrUpdateUserProgress(defaultUserProgress)
    }

    private suspend fun seedGameResults() {
        val fakeResults = DataProvider.getFakeGameResults()
        fakeResults.forEach { gameResult ->
            gameResultDao.insertGameResult(gameResult)
        }
    }

    fun clearAllData() {
        CoroutineScope(Dispatchers.IO).launch {
            gameResultDao.deleteAllGames()
        }
    }
}