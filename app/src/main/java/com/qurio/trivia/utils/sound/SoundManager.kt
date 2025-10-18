package com.qurio.trivia.utils.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.annotation.RawRes
import com.qurio.trivia.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManager @Inject constructor(
    private val context: Context
) {
    private var musicPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null
    private val soundIds = mutableMapOf<Int, Int>()

    private var musicVolume: Float = 0.6f
    private var soundVolume: Float = 0.8f

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
         //soundIds[SOUND_BUTTON_CLICK] = soundPool?.load(context, R.raw.button_click, 1) ?: 0
         soundIds[SOUND_BUTTON_CLICK] = soundPool?.load(context, R.raw.coins_sound, 1) ?: 0
         soundIds[SOUND_CORRECT] = soundPool?.load(context, R.raw.correct, 1) ?: 0
         soundIds[SOUND_WRONG] = soundPool?.load(context, R.raw.wrong, 1) ?: 0
            soundIds[SOUND_BACKGROUND] = soundPool?.load(context, R.raw.app_theme_1, 1) ?: 0

    }

    fun updateVolumes(newMusicVolume: Float, newSoundVolume: Float) {
        musicVolume = (newMusicVolume / 100f).coerceIn(0f, 1f)
        soundVolume = (newSoundVolume / 100f).coerceIn(0f, 1f)

        musicPlayer?.setVolume(musicVolume, musicVolume)
    }



    fun playBackgroundMusic(@RawRes resId: Int, loop: Boolean = true) {

        try {
            stopMusic()

            musicPlayer = MediaPlayer.create(context, resId).apply {
                isLooping = loop
                setVolume(musicVolume, musicVolume)
                setOnErrorListener { _, _, _ ->
                    release()
                    true
                }
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playSound(soundId: Int) {

        soundIds[soundId]?.let { id ->
            soundPool?.play(id, soundVolume, soundVolume, 1, 0, 1f)
        }
    }

    fun pauseMusic() {
        musicPlayer?.pause()
    }

    fun resumeMusic() {
        musicPlayer?.start()
    }

    fun stopMusic() {
        musicPlayer?.apply {
            stop()
            release()
        }
        musicPlayer = null
    }

    fun release() {
        stopMusic()
        soundPool?.release()
        soundPool = null
        soundIds.clear()
    }

    companion object {
        const val SOUND_BUTTON_CLICK = 1
        const val SOUND_CORRECT = 2
        const val SOUND_WRONG = 3
        const val SOUND_TICK = 4
        const val SOUND_BACKGROUND = 5
    }
}