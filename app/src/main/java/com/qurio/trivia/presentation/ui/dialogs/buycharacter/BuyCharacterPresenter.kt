package com.qurio.trivia.presentation.ui.dialogs.buycharacter

import android.util.Log
import com.qurio.trivia.domain.model.Character
import com.qurio.trivia.domain.repository.CharacterRepository
import com.qurio.trivia.presentation.base.BasePresenter
import javax.inject.Inject

class BuyCharacterPresenter @Inject constructor(
    private val repository: CharacterRepository
) : BasePresenter<BuyCharacterView>() {

    companion object {
        private const val TAG = "BuyCharacterPresenter"
    }

    // ========== Get User Coins ==========

    fun loadUserCoins() {
        tryToExecute(
            execute = {
                repository.getUserCoins()
            },
            onSuccess = { coins ->
                Log.d(TAG, "✓ User has $coins coins")
                withView { updateUserCoins(coins) }
            },
            onError = { error ->
                Log.e(TAG, "✗ Error loading user coins", error)
                withView { showError("Failed to load coins") }
            },
            showLoading = false
        )
    }

    // ========== Purchase Character ==========

    fun purchaseCharacter(character: Character) {
        Log.d(TAG, "Attempting to purchase: ${character.displayName}")

        tryToExecute(
            execute = {
                repository.purchaseCharacter(character)
            },
            onSuccess = { result ->
                handlePurchaseResult(result, character)
            },
            onError = { error ->
                Log.e(TAG, "✗ Error purchasing character", error)
                withView { showError("Purchase failed") }
            },
            showLoading = true
        )
    }

    private fun handlePurchaseResult(
        result: CharacterRepository.PurchaseResult,
        character: Character
    ) {
        when (result) {
            is CharacterRepository.PurchaseResult.Success -> {
                Log.d(TAG, "✓ Character purchased successfully. Remaining: ${result.remainingCoins}")
                withView {
                    onPurchaseSuccess(character, result.remainingCoins)
                }
            }
            is CharacterRepository.PurchaseResult.InsufficientCoins -> {
                Log.w(TAG, "✗ Insufficient coins: ${result.currentCoins}/${result.requiredCoins}")
                withView {
                    showInsufficientCoins(result.currentCoins, result.requiredCoins)
                }
            }
            is CharacterRepository.PurchaseResult.Error -> {
                Log.e(TAG, "✗ Purchase error: ${result.message}")
                withView { showError(result.message) }
            }
        }
    }
}