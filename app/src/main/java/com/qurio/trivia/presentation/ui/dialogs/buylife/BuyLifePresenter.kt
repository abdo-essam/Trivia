package com.qurio.trivia.presentation.ui.dialogs.buylife

import android.util.Log
import com.qurio.trivia.domain.model.PurchaseResult
import com.qurio.trivia.domain.repository.LifeRepository
import com.qurio.trivia.presentation.base.BasePresenter
import javax.inject.Inject

class BuyLifePresenter @Inject constructor(
    private val lifeRepository: LifeRepository
) : BasePresenter<BuyLifeView>() {

    companion object {
        private const val TAG = "BuyLifePresenter"
    }

    // ========== Load User Coins ==========

    fun loadUserCoins() {
        tryToExecute(
            execute = {
                lifeRepository.getUserCoins()
            },
            onSuccess = { coins ->
                Log.d(TAG, "User has $coins coins")
                withView { updateUserCoins(coins) }
            },
            onError = { error ->
                Log.e(TAG, "Error loading user coins", error)
                withView { showError("Failed to load coins") }
            },
            showLoading = false
        )
    }

    // ========== Purchase Life ==========

    fun purchaseLife(cost: Int) {
        tryToExecute(
            execute = {
                lifeRepository.purchaseLife(cost)
            },
            onSuccess = { result ->
                handlePurchaseResult(result)
            },
            onError = { error ->
                Log.e(TAG, "Error purchasing life", error)
                withView { showError("Purchase failed: ${error.message}") }
            },
            showLoading = true
        )
    }

    // ========== Handle Purchase Result ==========

    private fun handlePurchaseResult(result: PurchaseResult) {
        when (result) {
            is PurchaseResult.Success -> {
                Log.d(TAG, "Life purchased successfully. Remaining: ${result.remainingCoins} coins, ${result.remainingLives} lives")
                withView {
                    onPurchaseSuccess(result.remainingCoins, result.remainingLives)
                }
            }
            is PurchaseResult.InsufficientCoins -> {
                Log.d(TAG, "Insufficient coins: ${result.currentCoins}/${result.requiredCoins}")
                withView {
                    showInsufficientCoins(result.currentCoins, result.requiredCoins)
                }
            }
            is PurchaseResult.Error -> {
                Log.e(TAG, "Purchase error: ${result.message}")
                withView { showError(result.message) }
            }
        }
    }
}