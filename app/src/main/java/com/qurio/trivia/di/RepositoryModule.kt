package com.qurio.trivia.di

import android.content.SharedPreferences
import com.qurio.trivia.data.database.dao.CharacterDao
import com.qurio.trivia.data.database.dao.GameResultDao
import com.qurio.trivia.data.database.dao.UserProgressDao
import com.qurio.trivia.data.mapper.GameResultMapper
import com.qurio.trivia.data.mapper.UserProgressMapper
import com.qurio.trivia.data.remote.TriviaApiService
import com.qurio.trivia.data.repository.*
import com.qurio.trivia.domain.repository.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideHomeRepository(
        userProgressDao: UserProgressDao,
        gameResultDao: GameResultDao,
        userProgressMapper: UserProgressMapper,
        gameResultMapper: GameResultMapper
    ): HomeRepository {
        return HomeRepositoryImpl(
            userProgressDao,
            gameResultDao,
            userProgressMapper,
            gameResultMapper
        )
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
    fun provideGameResultRepository(
        gameResultDao: GameResultDao,
        userProgressDao: UserProgressDao,
        gameResultMapper: GameResultMapper,
        userProgressMapper: UserProgressMapper
    ): GameResultRepository {
        return GameResultRepositoryImpl(
            gameResultDao,
            userProgressDao,
            gameResultMapper,
            userProgressMapper
        )
    }

    @Provides
    @Singleton
    fun provideTriviaRepository(
        apiService: TriviaApiService
    ): TriviaRepository {
        return TriviaRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideAchievementsRepository(
        gameResultDao: GameResultDao,
        userProgressDao: UserProgressDao
    ): AchievementsRepository {
        return AchievementsRepositoryImpl(gameResultDao, userProgressDao)
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
    fun provideCharacterRepository(
        characterDao: CharacterDao,
        userProgressDao: UserProgressDao
    ): CharacterRepository {
        return CharacterRepositoryImpl(
            characterDao,
            userProgressDao
        )
    }

    @Provides
    @Singleton
    fun provideGameHistoryRepository(
        gameResultDao: GameResultDao,
        gameResultMapper: GameResultMapper
    ): GameHistoryRepository {
        return GameHistoryRepositoryImpl(gameResultDao, gameResultMapper)
    }
}