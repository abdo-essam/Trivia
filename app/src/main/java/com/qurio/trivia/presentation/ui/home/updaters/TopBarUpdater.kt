package com.qurio.trivia.presentation.ui.home.updaters

import android.content.Context
import com.qurio.trivia.R
import com.qurio.trivia.databinding.TopBarHomeBinding
import com.qurio.trivia.domain.model.Character
import com.qurio.trivia.domain.model.UserProgress

/**
 * Handles top bar UI updates
 */
class TopBarUpdater(
    private val binding: TopBarHomeBinding,
    private val context: Context
) {
    fun update(userProgress: UserProgress) {
        val character = Character.fromName(userProgress.selectedCharacter)
            ?: Character.default()

        binding.apply {
            tvWelcome.text = context.getString(R.string.welcome_qurio_explorer)
            tvCharacterName.text = character.displayName
            ivCharacter.setImageResource(character.imageRes)
        }
    }

    fun setOnSettingsClickListener(onClick: () -> Unit) {
        binding.btnSettings.setOnClickListener { onClick() }
    }

    fun setOnCharacterClickListener(onClick: () -> Unit) {
        binding.ivCharacter.setOnClickListener { onClick() }
    }
}