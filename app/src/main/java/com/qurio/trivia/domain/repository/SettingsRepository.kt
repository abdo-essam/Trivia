package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.Settings

interface SettingsRepository {
    suspend fun getSettings(): Settings
    suspend fun saveSettings(settings: Settings)
    suspend fun resetToDefaults(): Settings
}