package com.qurio.trivia.di

import android.content.SharedPreferences
import com.qurio.trivia.data.database.dao.CharacterDao
import com.qurio.trivia.data.database.dao.GameResultDao
import com.qurio.trivia.data.database.dao.UserAchievementDao
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
    fun provideStreakRepository(
        userProgressDao: UserProgressDao
    ): StreakRepository {
        return StreakRepositoryImpl(userProgressDao)
    }
    @Provides
    @Singleton
    fun provideUserRepository(
        userProgressDao: UserProgressDao,
        userProgressMapper: UserProgressMapper
    ): UserRepository {
        return UserRepositoryImpl(userProgressDao, userProgressMapper)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(): CategoryRepository {
        return CategoryRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideGameResultRepository(
        gameResultDao: GameResultDao,
        gameResultMapper: GameResultMapper
    ): GameResultRepository {
        return GameResultRepositoryImpl(gameResultDao, gameResultMapper)
    }

    @Provides
    @Singleton
    fun provideAchievementsRepository(
        achievementDao: UserAchievementDao,
        gameResultDao: GameResultDao,
        userProgressDao: UserProgressDao
    ): AchievementsRepository {
        return AchievementsRepositoryImpl(achievementDao, gameResultDao, userProgressDao)
    }

    @Provides
    @Singleton
    fun provideTriviaRepository(
        apiService: TriviaApiService
    ): TriviaRepository {
        return TriviaRepositoryImpl(apiService)
    }
}