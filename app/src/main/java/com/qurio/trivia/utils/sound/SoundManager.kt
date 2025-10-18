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

    private var tickingStreamId: Int = 0

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
        soundIds[Constants.Sound.SOUND_DIALOG_OPEN] = loadSound(R.raw.board_pop_up)
        soundIds[Constants.Sound.SOUND_DIALOG_CLOSE] = loadSound(R.raw.pop_sound)
        soundIds[Constants.Sound.SOUND_CLOCK_TICKING] = loadSound(R.raw.clock_ticking)
        soundIds[Constants.Sound.SOUND_CLOUD_SPREAD] = loadSound(R.raw.cloud_spread)
    }

    private fun loadSound(@RawRes resId: Int): Int {
        return soundPool?.load(context, resId, 1) ?: 0
    }

    fun updateVolume(newVolume: Float) {
        soundVolume = (newVolume / 100f).coerceIn(
            Constants.Settings.MIN_VOLUME / 100f,
            Constants.Settings.MAX_VOLUME / 100f
        )

        if (tickingStreamId != 0) {
            soundPool?.setVolume(tickingStreamId, soundVolume, soundVolume)
        }
    }

    fun playSound(soundId: Int) {
        soundIds[soundId]?.let { id ->
            soundPool?.play(id, soundVolume, soundVolume, 1, 0, 1f)
        }
    }

    fun playLoopingSound(soundId: Int) {
        stopLoopingSound()
        soundIds[soundId]?.let { id ->
            tickingStreamId = soundPool?.play(id, soundVolume, soundVolume, 1, -1, 1f) ?: 0
        }
    }

    fun stopLoopingSound() {
        if (tickingStreamId != 0) {
            soundPool?.stop(tickingStreamId)
            tickingStreamId = 0
        }
    }

    fun release() {
        stopLoopingSound()
        soundPool?.release()
        soundPool = null
        soundIds.clear()
    }
}