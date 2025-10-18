package com.qurio.trivia.presentation.ui.dialogs.settings

import com.qurio.trivia.domain.repository.SettingsRepository
import com.qurio.trivia.presentation.base.BasePresenter
import com.qurio.trivia.utils.Constants
import com.qurio.trivia.utils.sound.SoundManager
import javax.inject.Inject

class SettingsPresenter @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val soundManager: SoundManager
) : BasePresenter<SettingsView>() {

    private var initialVolume: Float? = null

    fun loadSettings() {
        tryToExecute(
            execute = { settingsRepository.getSoundVolume() },
            onSuccess = { volume ->
                initialVolume = volume
                soundManager.updateVolume(volume)
                withView { displayVolume(volume) }
            },
            onError = {
                withView {
                    showError("Failed to load settings")
                    displayVolume(Constants.Settings.DEFAULT_VOLUME)
                }
            },
            showLoading = false
        )
    }

    fun updateVolumePreview(volume: Float) {
        soundManager.updateVolume(volume)
    }

    fun restoreVolume(volume: Float) {
        soundManager.updateVolume(volume)
    }

    fun saveSettings(volume: Float) {
        if (!isValidVolume(volume)) {
            withView { showError("Invalid volume value") }
            return
        }

        tryToExecute(
            execute = {
                settingsRepository.saveSoundVolume(volume)
                soundManager.updateVolume(volume)
            },
            onSuccess = {
                initialVolume = volume
                withView { onSettingsSaved() }
            },
            onError = {
                withView {
                    initialVolume?.let { vol ->
                        soundManager.updateVolume(vol)
                    }
                    showError("Failed to save settings")
                }
            },
            showLoading = true
        )
    }

    private fun isValidVolume(volume: Float): Boolean {
        return volume in Constants.Settings.MIN_VOLUME..Constants.Settings.MAX_VOLUME
    }
}