package com.qurio.trivia.data.repository

import com.qurio.trivia.data.database.dao.UserProgressDao
import com.qurio.trivia.domain.repository.GameRepository
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val userProgressDao: UserProgressDao
) : GameRepository {

    override suspend fun updateLives(lives: Int) {
        userProgressDao.updateLives(lives)
    }

    override suspend fun updateCoins(coins: Int) {
        userProgressDao.updateCoins(coins)
    }

    override suspend fun getUserLives(): Int? {
        return userProgressDao.getUserProgress()?.lives
    }

    override suspend fun getUserCoins(): Int? {
        return userProgressDao.getUserProgress()?.totalCoins
    }
}