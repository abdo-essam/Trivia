package com.qurio.trivia.data.database

import com.qurio.trivia.data.database.dao.UserProgressDao
import com.qurio.trivia.data.database.entity.UserProgressEntity
import com.qurio.trivia.domain.model.Character
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val userProgressDao: UserProgressDao
) {
    suspend fun seedDatabase() = withContext(Dispatchers.IO) {
        try {
            seedUserProgress()
        } catch (e: Exception) { }
    }

    private suspend fun seedUserProgress() {
        if (userProgressDao.getUserProgress() == null) {
            userProgressDao.insertOrUpdateUserProgress(
                UserProgressEntity(
                    id = 1,
                    lives = 50,
                    totalCoins = 10001,
                    selectedCharacter = Character.default().characterName
                )
            )
        }
    }
}