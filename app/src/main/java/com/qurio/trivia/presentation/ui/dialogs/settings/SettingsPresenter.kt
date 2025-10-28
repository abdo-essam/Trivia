package com.qurio.trivia.presentation.ui.dialogs.settings

import com.qurio.trivia.domain.model.Settings
import com.qurio.trivia.domain.repository.SettingsRepository
import com.qurio.trivia.presentation.base.BasePresenter
import com.qurio.trivia.utils.sound.SoundManager
import javax.inject.Inject

class SettingsPresenter @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val soundManager: SoundManager
) : BasePresenter<SettingsView>() {

    private var initialSettings: Settings? = null

    fun loadSettings() {
        tryToExecute(
            execute = { settingsRepository.getSettings() },
            onSuccess = { settings ->
                initialSettings = settings
                soundManager.updateVolume(settings.soundVolume)
                withView { displaySettings(settings) }
            },
            onError = {
                withView {
                    showError("Failed to load settings")
                    displaySettings(Settings.DEFAULT)
                }
            },
            showLoading = false
        )
    }

    fun updateVolumesPreview(soundVolume: Float, musicVolume: Float) {
        soundManager.updateVolume(soundVolume)
    }

    fun restoreSettings(soundVolume: Float, musicVolume: Float) {
        soundManager.updateVolume(soundVolume)
    }

    fun saveSettings(soundVolume: Float, musicVolume: Float) {
        val newSettings = Settings(soundVolume, musicVolume)

        if (!newSettings.isValid()) {
            withView { showError("Invalid settings values") }
            return
        }

        tryToExecute(
            execute = {
                settingsRepository.saveSettings(newSettings)
                soundManager.updateVolume(soundVolume)
            },
            onSuccess = {
                initialSettings = newSettings
                withView { onSettingsSaved() }
            },
            onError = {
                withView {
                    initialSettings?.let { settings ->
                        soundManager.updateVolume(settings.soundVolume)
                    }
                    showError("Failed to save settings")
                }
            },
            showLoading = true
        )
    }
}