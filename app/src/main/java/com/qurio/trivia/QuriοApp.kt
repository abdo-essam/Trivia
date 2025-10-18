package com.qurio.trivia

import android.app.Application
import com.qurio.trivia.di.AppComponent
import com.qurio.trivia.di.DaggerAppComponent
import com.qurio.trivia.utils.sound.SoundManager
import javax.inject.Inject

class QurioApp : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }

    @Inject
    lateinit var soundManager: SoundManager

    override fun onCreate() {
        super.onCreate()

        appComponent.inject(this)
    }


    override fun onTerminate() {
        soundManager.release()
        super.onTerminate()
    }
}