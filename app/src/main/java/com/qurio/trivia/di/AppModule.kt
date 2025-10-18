package com.qurio.trivia.di

import android.content.Context
import android.content.SharedPreferences
import com.qurio.trivia.utils.PreferenceKeys.PREFS_NAME
import com.qurio.trivia.utils.sound.SoundManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    companion object {
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideSoundManager(context: Context): SoundManager {
        return SoundManager(context)
    }
}