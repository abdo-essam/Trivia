package com.qurio.trivia.data.mapper

import com.qurio.trivia.data.database.entity.UserProgressEntity
import com.qurio.trivia.domain.model.UserProgress
import javax.inject.Inject

class UserProgressMapper @Inject constructor() {

    fun toDomain(entity: UserProgressEntity): UserProgress {
        return UserProgress(
            lives = entity.lives,
            totalCoins = entity.totalCoins,
            selectedCharacter = entity.selectedCharacter,
            currentStreak = entity.currentStreak,
            lastPlayedDate = entity.lastPlayedDate,
            streakDays = entity.streakDays
        )
    }
}