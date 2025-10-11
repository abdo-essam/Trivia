package com.qurio.trivia.data.repository

import com.qurio.trivia.data.model.TriviaResponse


interface TriviaRepository {
    suspend fun getQuestions(
        amount: Int,
        category: Int?,
        difficulty: String?
    ): Result<TriviaResponse>
}