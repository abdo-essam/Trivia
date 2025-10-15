package com.qurio.trivia

import android.app.Application
import com.qurio.trivia.di.AppComponent
import com.qurio.trivia.di.DaggerAppComponent

class QuriοApp : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()

        appComponent.inject(this)

    }
}