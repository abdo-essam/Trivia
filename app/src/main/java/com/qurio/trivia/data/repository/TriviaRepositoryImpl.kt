package com.qurio.trivia.data.repository

import com.qurio.trivia.data.model.TriviaResponse
import com.qurio.trivia.data.remote.TriviaApiService
import com.qurio.trivia.domain.repository.TriviaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TriviaRepositoryImpl @Inject constructor(
    private val apiService: TriviaApiService
) : TriviaRepository {

    override suspend fun getQuestions(
        amount: Int,
        category: Int?,
        difficulty: String?
    ): Result<TriviaResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getQuestions(
                amount = amount,
                category = category,
                difficulty = difficulty
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch questions"))
            }
        } catch (e: UnknownHostException) {
            Result.failure(Exception("No internet connection"))
        } catch (e: Exception) {
            Result.failure(Exception("An error occurred"))
        }
    }
}