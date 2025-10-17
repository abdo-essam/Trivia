package com.qurio.trivia.presentation.ui.dialogs.characterinfo.manager

import com.qurio.trivia.databinding.DialogCharacterInfoBinding
import com.qurio.trivia.domain.model.Character
import com.qurio.trivia.presentation.mapper.infoImageRes

class CharacterInfoUIManager(
    private val binding: DialogCharacterInfoBinding
) {

    fun displayCharacterInfo(character: Character) {
        binding.apply {
            tvCharacterName.text = character.displayName
            tvCharacterAge.text = character.age
            tvCharacterDescription.text = character.description
            ivCharacter.setImageResource(character.infoImageRes())
        }
    }
}