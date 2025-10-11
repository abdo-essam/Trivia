package com.qurio.trivia.presentation.ui.dialogs.buycharacter

import android.util.Log
import com.qurio.trivia.presentation.base.BasePresenter
import com.qurio.trivia.data.repository.CharacterRepository
import javax.inject.Inject

class BuyCharacterPresenter @Inject constructor(
    private val repository: CharacterRepository
) : BasePresenter<BuyCharacterView>() {

    // ========== Get User Coins ==========

    fun loadUserCoins() {
        tryToExecute(
            execute = {
                repository.getUserCoins()
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

    // ========== Purchase Character ==========

    fun purchaseCharacter(characterName: String, cost: Int) {
        tryToExecute(
            execute = {
                repository.purchaseCharacter(characterName, cost)
            },
            onSuccess = { result ->
                handlePurchaseResult(result, characterName)
            },
            onError = { error ->
                Log.e(TAG, "Error purchasing character", error)
                withView { showError("Purchase failed") }
            },
            showLoading = true
        )
    }

    private fun handlePurchaseResult(
        result: CharacterRepository.PurchaseResult,
        characterName: String
    ) {
        when (result) {
            is CharacterRepository.PurchaseResult.Success -> {
                Log.d(TAG, "Character purchased successfully. Remaining coins: ${result.remainingCoins}")
                withView {
                    onPurchaseSuccess(characterName, result.remainingCoins)
                }
            }
            is CharacterRepository.PurchaseResult.InsufficientCoins -> {
                Log.d(TAG, "Insufficient coins: ${result.currentCoins}/${result.requiredCoins}")
                withView {
                    showInsufficientCoins(result.currentCoins, result.requiredCoins)
                }
            }
            is CharacterRepository.PurchaseResult.Error -> {
                Log.e(TAG, "Purchase error: ${result.message}")
                withView { showError(result.message) }
            }
        }
    }

    companion object {
        private const val TAG = "BuyCharacterPresenter"
    }
}