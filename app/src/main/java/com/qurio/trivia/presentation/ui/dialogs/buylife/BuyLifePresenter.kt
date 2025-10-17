package com.qurio.trivia.presentation.ui.dialogs.buylife

import com.qurio.trivia.domain.model.PurchaseResult
import com.qurio.trivia.domain.repository.LifeRepository
import com.qurio.trivia.presentation.base.BasePresenter
import javax.inject.Inject

class BuyLifePresenter @Inject constructor(
    private val lifeRepository: LifeRepository
) : BasePresenter<BuyLifeView>() {

    fun loadUserCoins() {
        tryToExecute(
            execute = {
                lifeRepository.getUserCoins()
            },
            onSuccess = { coins ->
                withView { updateUserCoins(coins) }
            },
            onError = { error ->
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
                withView { showError("Purchase failed: ${error.message}") }
            },
            showLoading = true
        )
    }

    // ========== Handle Purchase Result ==========

    private fun handlePurchaseResult(result: PurchaseResult) {
        when (result) {
            is PurchaseResult.Success -> {
                withView {
                    onPurchaseSuccess(result.remainingCoins, result.remainingLives)
                }
            }
            is PurchaseResult.InsufficientCoins -> {
                withView {
                    showInsufficientCoins(result.currentCoins, result.requiredCoins)
                }
            }
            is PurchaseResult.Error -> {
                withView { showError(result.message) }
            }
        }
    }
}