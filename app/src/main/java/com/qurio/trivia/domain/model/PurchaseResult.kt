package com.qurio.trivia.domain.model

sealed class PurchaseResult {
    data class Success(
        val remainingCoins: Int,
        val remainingLives: Int
    ) : PurchaseResult()

    data class InsufficientCoins(
        val currentCoins: Int,
        val requiredCoins: Int
    ) : PurchaseResult()

    data class Error(
        val message: String
    ) : PurchaseResult()
}