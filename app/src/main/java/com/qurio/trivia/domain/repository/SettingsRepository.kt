package com.qurio.trivia.domain.repository

interface SettingsRepository {
    suspend fun getSoundVolume(): Float
    suspend fun saveSoundVolume(volume: Float)

}