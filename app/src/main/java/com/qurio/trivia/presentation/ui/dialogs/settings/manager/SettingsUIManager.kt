package com.qurio.trivia.presentation.ui.dialogs.settings.manager

import com.qurio.trivia.databinding.DialogSettingsBinding
import com.qurio.trivia.domain.model.Settings

class SettingsUIManager(
    private val binding: DialogSettingsBinding
) {

    fun displaySettings(settings: Settings) {
        binding.apply {
            seekbarSound.progress = settings.soundVolume.toInt()
            seekbarMusic.progress = settings.musicVolume.toInt()
        }
    }

    fun getCurrentSettings(): Settings {
        return Settings(
            soundVolume = binding.seekbarSound.progress.toFloat(),
            musicVolume = binding.seekbarMusic.progress.toFloat()
        )
    }

    fun setControlsEnabled(enabled: Boolean) {
        binding.apply {
            btnSave.isEnabled = enabled
            btnDiscard.isEnabled = enabled
            seekbarSound.isEnabled = enabled
            seekbarMusic.isEnabled = enabled
        }
    }
}