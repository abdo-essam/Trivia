package com.qurio.trivia.di

import android.content.SharedPreferences
import com.qurio.trivia.data.database.GameResultDao
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.data.remote.TriviaApiService
import com.qurio.trivia.data.repository.*
import com.qurio.trivia.domain.repository.LifeRepository
import com.qurio.trivia.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideHomeRepository(
        userProgressDao: UserProgressDao,
        gameResultDao: GameResultDao
    ): HomeRepository {
        return HomeRepository(userProgressDao, gameResultDao)
    }

    @Provides
    @Singleton
    fun provideGamesRepository(
        userProgressDao: UserProgressDao
    ): GamesRepository {
        return GamesRepository(userProgressDao)
    }

    @Provides
    @Singleton
    fun provideLastGamesRepository(
        gameResultDao: GameResultDao
    ): LastGamesRepository {
        return LastGamesRepository(gameResultDao)
    }

    @Provides
    @Singleton
    fun provideGameResultRepository(
        gameResultDao: GameResultDao,
        userProgressDao: UserProgressDao
    ): GameResultRepository {
        return GameResultRepository(gameResultDao, userProgressDao)
    }

    @Provides
    @Singleton
    fun provideAchievementsRepository(
        gameResultDao: GameResultDao,
        userProgressDao: UserProgressDao
    ): AchievementsRepository {
        return AchievementsRepository(gameResultDao, userProgressDao)
    }

    @Provides
    @Singleton
    fun provideLifeRepository(
        userProgressDao: UserProgressDao
    ): LifeRepository {
        return LifeRepositoryImpl(userProgressDao)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        sharedPreferences: SharedPreferences
    ): SettingsRepository {
        return SettingsRepositoryImpl(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideTriviaRepository(
        apiService: TriviaApiService
    ): TriviaRepository {
        return TriviaRepositoryImpl(apiService)
    }
}