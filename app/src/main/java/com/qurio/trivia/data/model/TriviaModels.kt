package com.qurio.trivia.data.model

import com.google.gson.annotations.SerializedName

data class TriviaResponse(
    @SerializedName("response_code") val responseCode: Int,
    @SerializedName("results") val results: List<TriviaQuestion>
)

data class TriviaQuestion(
    val category: String,
    val type: String,
    val difficulty: String,
    val question: String,
    @SerializedName("correct_answer") val correctAnswer: String,
    @SerializedName("incorrect_answers") val incorrectAnswers: List<String>
) {
    /**
     * Returns all answers shuffled
     */
    fun getAllAnswers(): List<String> {
        return (incorrectAnswers + correctAnswer).shuffled()
    }

    /**
     * Returns the question with HTML entities decoded
     */
    fun getDecodedQuestion(): String {
        return decodeHtml(question)
    }

    /**
     * Returns all answers with HTML entities decoded
     */
    fun getDecodedAnswers(): List<String> {
        return getAllAnswers().map { decodeHtml(it) }
    }

    private fun decodeHtml(text: String): String {
        return text
            .replace("&quot;", "\"")
            .replace("&#039;", "'")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
    }
}