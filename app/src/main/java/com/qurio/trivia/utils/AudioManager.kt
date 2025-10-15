package com.qurio.trivia.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.qurio.trivia.domain.model.Settings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioManager @Inject constructor(
    private val context: Context
) {
    private var soundPool: SoundPool? = null
    private var currentSettings: Settings = Settings.DEFAULT

    init {
        initializeSoundPool()
    }

    private fun initializeSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()
    }

    fun updateSettings(settings: Settings) {
        currentSettings = settings
        // Update actual audio volumes
    }

    fun playSound(soundId: Int) {
        val volume = currentSettings.soundVolume / 100f
        soundPool?.play(soundId, volume, volume, 1, 0, 1f)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}