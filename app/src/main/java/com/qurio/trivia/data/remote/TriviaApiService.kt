package com.qurio.trivia.data.remote

import com.qurio.trivia.data.model.TriviaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApiService {
    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int = 12,
        @Query("category") category: Int? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("type") type: String = "multiple",
       // @Query("encode") encode: String = "url3986"
    ): Response<TriviaResponse>
}