package com.qurio.trivia.data.repository

import com.qurio.trivia.data.model.TriviaResponse
import com.qurio.trivia.data.network.TriviaApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TriviaRepositoryImpl @Inject constructor(
    private val api: TriviaApi
) : TriviaRepository {

    override suspend fun getQuestions(amount: Int, category: Int?, difficulty: String?): Result<TriviaResponse> {
        return try {
            val response = api.getQuestions(amount, category, difficulty)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load questions"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}