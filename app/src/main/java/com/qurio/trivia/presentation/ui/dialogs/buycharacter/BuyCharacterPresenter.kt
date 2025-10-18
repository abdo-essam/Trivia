package com.qurio.trivia.presentation.ui.dialogs.buycharacter

import com.qurio.trivia.domain.model.Character
import com.qurio.trivia.domain.repository.CharacterRepository
import com.qurio.trivia.domain.repository.UserRepository
import com.qurio.trivia.presentation.base.BasePresenter
import javax.inject.Inject

class BuyCharacterPresenter @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val userRepository: UserRepository
) : BasePresenter<BuyCharacterView>() {

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

    fun purchaseCharacter(character: Character) {
        tryToExecute(
            execute = {
                characterRepository.purchaseCharacter(character)
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