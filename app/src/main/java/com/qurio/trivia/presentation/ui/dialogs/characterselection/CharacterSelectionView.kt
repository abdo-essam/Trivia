package com.qurio.trivia.presentation.ui.dialogs.characterselection

import com.qurio.trivia.domain.model.Character
import com.qurio.trivia.domain.repository.CharacterRepository
import com.qurio.trivia.presentation.base.BaseView

interface CharacterSelectionView : BaseView {
    fun displayCharacters(characters: List<CharacterRepository.CharacterWithStatus>)
    fun onCharacterSaved(character: Character)
    fun onCharacterPurchased(character: Character)
}