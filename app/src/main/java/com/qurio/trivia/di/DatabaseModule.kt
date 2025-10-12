package com.qurio.trivia.di

import android.content.Context
import androidx.room.Room
import com.qurio.trivia.data.database.AppDatabase
import com.qurio.trivia.data.database.dao.CharacterDao
import com.qurio.trivia.data.database.dao.GameResultDao
import com.qurio.trivia.data.database.dao.UserProgressDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideGameResultDao(database: AppDatabase): GameResultDao {
        return database.gameResultDao()
    }

    @Provides
    fun provideUserProgressDao(database: AppDatabase): UserProgressDao {
        return database.userProgressDao()
    }

    @Provides
    fun provideCharacterDao(database: AppDatabase): CharacterDao {
        return database.characterDao()
    }
}