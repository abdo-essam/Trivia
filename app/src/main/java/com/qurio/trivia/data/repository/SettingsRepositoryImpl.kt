package com.qurio.trivia.data.repository

import android.content.SharedPreferences
import com.qurio.trivia.domain.repository.SettingsRepository
import com.qurio.trivia.utils.Constants
import com.qurio.trivia.utils.PreferenceKeys.SOUND_VOLUME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    override suspend fun getSoundVolume(): Float = withContext(Dispatchers.IO) {
        sharedPreferences.getFloat(
            SOUND_VOLUME,
            Constants.Settings.DEFAULT_VOLUME
        )
    }

    override suspend fun saveSoundVolume(volume: Float) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().apply {
                putFloat(SOUND_VOLUME, volume)
                apply()
            }
        }
    }
}