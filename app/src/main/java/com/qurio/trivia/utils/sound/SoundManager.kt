package com.qurio.trivia.utils.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes
import com.qurio.trivia.R
import com.qurio.trivia.utils.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManager @Inject constructor(
    private val context: Context
) {
    private var soundPool: SoundPool? = null
    private val soundIds = mutableMapOf<Int, Int>()
    private var soundVolume: Float = Constants.Settings.DEFAULT_VOLUME / 100f

    init {
        initializeSoundPool()
        loadSounds()
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

    private fun loadSounds() {
        soundIds[Constants.Sound.SOUND_CORRECT] = loadSound(R.raw.correct)
        soundIds[Constants.Sound.SOUND_WRONG] = loadSound(R.raw.wrong)
        soundIds[Constants.Sound.SOUND_COINS] = loadSound(R.raw.coins_sound)
        soundIds[Constants.Sound.SOUND_DIALOG_OPEN] = loadSound(R.raw.board_pop_up)
        soundIds[Constants.Sound.SOUND_DIALOG_CLOSE] = loadSound(R.raw.pop_sound)
    }

    private fun loadSound(@RawRes resId: Int): Int {
        return soundPool?.load(context, resId, 1) ?: 0
    }

    fun updateVolume(newVolume: Float) {
        soundVolume = (newVolume / 100f).coerceIn(
            Constants.Settings.MIN_VOLUME / 100f,
            Constants.Settings.MAX_VOLUME / 100f
        )
    }

    fun playSound(soundId: Int) {
        soundIds[soundId]?.let { id ->
            soundPool?.play(id, soundVolume, soundVolume, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        soundIds.clear()
    }
}