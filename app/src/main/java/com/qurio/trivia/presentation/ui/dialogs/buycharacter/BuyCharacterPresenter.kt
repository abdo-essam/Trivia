package com.qurio.trivia.presentation.ui.dialogs.buycharacter

import com.qurio.trivia.domain.model.Character
import com.qurio.trivia.domain.repository.CharacterRepository
import com.qurio.trivia.presentation.base.BasePresenter
import javax.inject.Inject

class BuyCharacterPresenter @Inject constructor(
    private val repository: CharacterRepository
) : BasePresenter<BuyCharacterView>() {

    fun loadUserCoins() {
        tryToExecute(
            execute = {
                repository.getUserCoins()
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

    fun purchaseCharacter(character: Character) {
        tryToExecute(
            execute = {
                repository.purchaseCharacter(character)
            },
            onSuccess = { result ->
                handlePurchaseResult(result, character)
            },
            onError = {
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
                withView { onPurchaseSuccess(character, result.remainingCoins) }
            }
            is CharacterRepository.PurchaseResult.InsufficientCoins -> {
                withView {
                    showInsufficientCoins(result.currentCoins, result.requiredCoins)
                }
            }
            is CharacterRepository.PurchaseResult.Error -> {
                withView { showError(result.message) }
            }
        }
    }
}