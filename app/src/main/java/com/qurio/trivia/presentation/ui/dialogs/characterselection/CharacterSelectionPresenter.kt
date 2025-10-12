package com.qurio.trivia.presentation.ui.dialogs.characterselection

import android.util.Log
import com.qurio.trivia.domain.repository.CharacterRepository
import com.qurio.trivia.presentation.base.BasePresenter
import javax.inject.Inject

/**
 * Presenter for CharacterSelectionDialog
 * Handles character loading and selection logic
 */
class CharacterSelectionPresenter @Inject constructor(
    private val characterRepository: CharacterRepository
) : BasePresenter<CharacterSelectionView>() {

    companion object {
        private const val TAG = "CharacterSelectionPresenter"
    }

    // ========== Load Characters ==========

    /**
     * Load all characters with lock status
     */
    fun loadCharacters() {
        tryToExecute(
            execute = {
                characterRepository.getAllCharacters()
            },
            onSuccess = { characters ->
                Log.d(TAG, "✓ Loaded ${characters.size} characters")
                withView { displayCharacters(characters) }
            },
            onError = { error ->
                Log.e(TAG, "✗ Failed to load characters", error)
                withView { showError("Failed to load characters") }
            },
            showLoading = true
        )
    }

    // ========== Save Selection ==========

    /**
     * Save selected character to user preferences
     */
    fun saveSelectedCharacter(characterName: String) {
        Log.d(TAG, "Saving character selection: $characterName")

        tryToExecute(
            execute = {
                characterRepository.selectCharacter(characterName)
                characterRepository.getCharacterByName(characterName)
            },
            onSuccess = { character ->
                character?.let {
                    Log.d(TAG, "✓ Character saved: ${it.displayName}")
                    withView { onCharacterSaved(it) }
                } ?: run {
                    Log.e(TAG, "✗ Character not found after save")
                    withView { showError("Failed to save character") }
                }
            },
            onError = { error ->
                Log.e(TAG, "✗ Failed to save character", error)
                withView { showError("Failed to save character") }
            },
            showLoading = true
        )
    }

    // ========== Purchase Callback ==========

    /**
     * Handle character purchase completion
     */
    fun onCharacterPurchased(characterName: String) {
        Log.d(TAG, "Character purchased: $characterName, refreshing list")
        withView { onCharacterPurchased(characterName) }
    }
}