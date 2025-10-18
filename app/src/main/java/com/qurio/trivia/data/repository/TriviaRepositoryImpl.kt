package com.qurio.trivia.data.repository

import com.qurio.trivia.data.model.TriviaResponse
import com.qurio.trivia.data.remote.TriviaApiService
import com.qurio.trivia.domain.repository.TriviaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
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
                val triviaResponse = response.body()!!
                handleApiResponse(triviaResponse)
            } else {
                Result.failure(
                    ApiException("HTTP ${response.code()}: ${response.message()}")
                )
            }
        } catch (e: IOException) {
            Result.failure(NetworkException("Network error occurred", e))
        } catch (e: Exception) {
            Result.failure(ApiException("Unexpected error: ${e.message}"))
        }
    }

    private fun handleApiResponse(response: TriviaResponse): Result<TriviaResponse> {
        return when (response.responseCode) {
            RESPONSE_CODE_SUCCESS -> Result.success(response)
            RESPONSE_CODE_NO_RESULTS -> Result.failure(
                NoQuestionsException("No questions found for the given parameters")
            )
            RESPONSE_CODE_INVALID_PARAMETER -> Result.failure(
                InvalidParameterException("Invalid parameter in request")
            )
            RESPONSE_CODE_TOKEN_NOT_FOUND -> Result.failure(
                TokenNotFoundException("Session token not found")
            )
            RESPONSE_CODE_TOKEN_EMPTY -> Result.failure(
                TokenEmptyException("Session token has returned all possible questions")
            )
            else -> Result.failure(
                UnknownApiException("Unknown API response code: ${response.responseCode}")
            )
        }
    }

    companion object {
        private const val RESPONSE_CODE_SUCCESS = 0
        private const val RESPONSE_CODE_NO_RESULTS = 1
        private const val RESPONSE_CODE_INVALID_PARAMETER = 2
        private const val RESPONSE_CODE_TOKEN_NOT_FOUND = 3
        private const val RESPONSE_CODE_TOKEN_EMPTY = 4
    }
}