package com.qurio.trivia

import android.app.Application
import com.qurio.trivia.di.AppComponent
import com.qurio.trivia.di.DaggerAppComponent
import com.qurio.trivia.utils.PreferenceKeys.MUSIC_VOLUME
import com.qurio.trivia.utils.PreferenceKeys.PREFS_NAME
import com.qurio.trivia.utils.PreferenceKeys.SOUND_VOLUME
import com.qurio.trivia.utils.sound.SoundManager
import javax.inject.Inject

class QuriοApp : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }

    @Inject
    lateinit var soundManager: SoundManager

    override fun onCreate() {
        super.onCreate()

        appComponent.inject(this)

        loadSoundSettings()

    }

    private fun loadSoundSettings() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val musicVolume = prefs.getFloat(MUSIC_VOLUME, 60f)
        val soundVolume = prefs.getFloat(SOUND_VOLUME, 80f)

        soundManager.updateVolumes(musicVolume, soundVolume)
    }

    override fun onTerminate() {
        soundManager.release()
        super.onTerminate()
    }

}