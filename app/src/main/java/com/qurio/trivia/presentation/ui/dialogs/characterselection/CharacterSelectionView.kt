package com.qurio.trivia.presentation.ui.dialogs.characterselection

import com.qurio.trivia.domain.model.Character
import com.qurio.trivia.presentation.base.BaseView

/**
 * View contract for CharacterSelectionDialog
 */
interface CharacterSelectionView : BaseView {
    /**
     * Display list of characters
     */
    fun displayCharacters(characters: List<Character>)

    /**
     * Called when character is successfully saved
     */
    fun onCharacterSaved(character: Character)

    /**
     * Called when a character is purchased
     */
    fun onCharacterPurchased(characterName: String)
}