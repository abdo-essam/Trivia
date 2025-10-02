package com.qurio.trivia.di

import com.qurio.trivia.data.repository.TriviaRepository
import com.qurio.trivia.data.repository.TriviaRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindTriviaRepository(
        triviaRepositoryImpl: TriviaRepositoryImpl
    ): TriviaRepository
}