package com.qurio.trivia.data.mapper

import com.qurio.trivia.data.database.entity.UserProgressEntity
import com.qurio.trivia.domain.model.UserProgress
import javax.inject.Inject

class UserProgressMapper @Inject constructor() {

    fun toDomain(entity: UserProgressEntity): UserProgress {
        return UserProgress(
            lives = entity.lives,
            totalCoins = entity.totalCoins,
            awards = entity.awards,
            selectedCharacter = entity.selectedCharacter,
            soundEnabled = entity.soundEnabled,
            musicEnabled = entity.musicEnabled,
            currentStreak = entity.currentStreak,
            lastPlayedDate = entity.lastPlayedDate,
            streakDays = entity.streakDays
        )
    }

    fun toEntity(domain: UserProgress): UserProgressEntity {
        return UserProgressEntity(
            id = 1,
            lives = domain.lives,
            totalCoins = domain.totalCoins,
            awards = domain.awards,
            selectedCharacter = domain.selectedCharacter,
            soundEnabled = domain.soundEnabled,
            musicEnabled = domain.musicEnabled,
            currentStreak = domain.currentStreak,
            lastPlayedDate = domain.lastPlayedDate,
            streakDays = domain.streakDays
        )
    }
}