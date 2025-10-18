package com.qurio.trivia.data.repository

import android.content.SharedPreferences
import com.qurio.trivia.domain.model.Settings
import com.qurio.trivia.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    override suspend fun getSettings(): Settings = withContext(Dispatchers.IO) {
        Settings(
            soundVolume = sharedPreferences.getFloat(KEY_SOUND_VOLUME, Settings.DEFAULT.soundVolume),
            musicVolume = sharedPreferences.getFloat(KEY_MUSIC_VOLUME, Settings.DEFAULT.musicVolume)
        )
    }

    override suspend fun saveSettings(settings: Settings) {
        sharedPreferences.edit().apply {
            putFloat(KEY_SOUND_VOLUME, settings.soundVolume)
            putFloat(KEY_MUSIC_VOLUME, settings.musicVolume)
            apply()
        }
    }

    companion object {
        private const val KEY_SOUND_VOLUME = "sound_volume"
        private const val KEY_MUSIC_VOLUME = "music_volume"
    }

}