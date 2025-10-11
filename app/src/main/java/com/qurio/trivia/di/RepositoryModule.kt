package com.qurio.trivia.di

import com.qurio.trivia.data.database.GameResultDao
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.data.remote.TriviaApiService
import com.qurio.trivia.data.repository.*
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
    fun provideTriviaRepository(
        apiService: TriviaApiService
    ): TriviaRepository {
        return TriviaRepositoryImpl(apiService)
    }
}