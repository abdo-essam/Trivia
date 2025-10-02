package com.qurio.trivia.data.network

import com.qurio.trivia.data.model.TriviaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApi {
    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int = 12,
        @Query("category") category: Int? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("type") type: String = "multiple"
    ): Response<TriviaResponse>

    @GET("api_token.php")
    suspend fun getSessionToken(
        @Query("command") command: String = "request"
    ): Response<Any>
}