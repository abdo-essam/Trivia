package com.qurio.trivia.data.repository

import android.content.SharedPreferences
import com.qurio.trivia.domain.model.Settings
import com.qurio.trivia.domain.repository.SettingsRepository
import com.qurio.trivia.utils.Constants
import com.qurio.trivia.utils.Constants.Settings.DEFAULT_SOUND_VOLUME
import com.qurio.trivia.utils.PreferenceKeys.KEY_MUSIC_VOLUME
import com.qurio.trivia.utils.PreferenceKeys.KEY_SOUND_VOLUME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    override suspend fun getSettings(): Settings = withContext(Dispatchers.IO) {
        Settings(
            soundVolume = getSoundVolume(),
            musicVolume = getMusicVolume()
        )
    }

    override suspend fun saveSettings(settings: Settings) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().apply {
                putFloat(KEY_SOUND_VOLUME, settings.soundVolume)
                putFloat(KEY_MUSIC_VOLUME, settings.musicVolume)
                apply()
            }
        }
    }

    override suspend fun getSoundVolume(): Float {
        return sharedPreferences.getFloat(
            KEY_SOUND_VOLUME,
            DEFAULT_SOUND_VOLUME
        )
    }

    override suspend fun getMusicVolume(): Float {
        return sharedPreferences.getFloat(
            KEY_MUSIC_VOLUME,
            Constants.Settings.DEFAULT_MUSIC_VOLUME
        )
    }

    override suspend fun saveSoundVolume(volume: Float) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().apply {
                putFloat(KEY_SOUND_VOLUME, volume)
                apply()
            }
        }
    }

    override suspend fun saveMusicVolume(volume: Float) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().apply {
                putFloat(KEY_MUSIC_VOLUME, volume)
                apply()
            }
        }
    }
}