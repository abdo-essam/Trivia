package com.qurio.trivia.presentation.ui.dialogs.buylife

import com.qurio.trivia.domain.model.PurchaseResult
import com.qurio.trivia.domain.repository.UserRepository
import com.qurio.trivia.presentation.base.BasePresenter
import javax.inject.Inject

class BuyLifePresenter @Inject constructor(
    private val userRepository: UserRepository
) : BasePresenter<BuyLifeView>() {

    fun loadUserCoins() {
        tryToExecute(
            execute = {
                userRepository.getUserCoins()
            },
            onSuccess = { coins ->
                withView { updateUserCoins(coins) }
            },
            onError = {
                withView { showError("Failed to load coins") }
            },
            showLoading = false
        )
    }

    fun purchaseLife(cost: Int) {
        tryToExecute(
            execute = {
                userRepository.purchaseLife(cost)
            },
            onSuccess = { result ->
                handlePurchaseResult(result)
            },
            onError = {
                withView { showError("Purchase Failed") }
            },
            showLoading = true
        )
    }

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