package com.qurio.trivia.di

import com.qurio.trivia.data.mapper.CharacterMapper
import com.qurio.trivia.data.mapper.GameResultMapper
import com.qurio.trivia.data.mapper.UserProgressMapper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class MapperModule {
    @Provides
    @Singleton
    fun provideUserProgressMapper(): UserProgressMapper {
        return UserProgressMapper()
    }

    @Provides
    @Singleton
    fun provideGameResultMapper(): GameResultMapper {
        return GameResultMapper()
    }

    @Provides
    @Singleton
    fun provideCharacterMapper(): CharacterMapper {
        return CharacterMapper()
    }
}

