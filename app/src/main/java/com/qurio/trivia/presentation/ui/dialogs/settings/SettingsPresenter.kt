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
                soundManager.updateVolumes(settings.soundVolume, settings.musicVolume)
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
        soundManager.updateVolumes(soundVolume, musicVolume)
    }

    fun restoreSettings(soundVolume: Float, musicVolume: Float) {
        soundManager.updateVolumes(soundVolume, musicVolume)
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
                soundManager.updateVolumes(soundVolume, musicVolume)
            },
            onSuccess = {
                initialSettings = newSettings
                withView { onSettingsSaved() }
            },
            onError = {
                withView {
                    initialSettings?.let { settings ->
                        soundManager.updateVolumes(settings.soundVolume, settings.musicVolume)
                    }
                    showError("Failed to save settings")
                }
            },
            showLoading = true
        )
    }
}