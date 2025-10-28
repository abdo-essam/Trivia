package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.Settings

interface SettingsRepository {
    suspend fun getSettings(): Settings
    suspend fun saveSettings(settings: Settings)
    suspend fun getSoundVolume(): Float
    suspend fun getMusicVolume(): Float
    suspend fun saveSoundVolume(volume: Float)
    suspend fun saveMusicVolume(volume: Float)
}