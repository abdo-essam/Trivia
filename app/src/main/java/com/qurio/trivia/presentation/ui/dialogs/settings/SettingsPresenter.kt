package com.qurio.trivia.presentation.ui.dialogs.settings

import com.qurio.trivia.domain.model.Settings
import com.qurio.trivia.domain.repository.SettingsRepository
import com.qurio.trivia.presentation.base.BasePresenter
import javax.inject.Inject

class SettingsPresenter @Inject constructor(
    private val settingsRepository: SettingsRepository
) : BasePresenter<SettingsView>() {

    private var initialSettings: Settings? = null

    fun loadSettings() {
        tryToExecute(
            execute = { settingsRepository.getSettings() },
            onSuccess = { settings ->
                initialSettings = settings
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

    fun saveSettings(soundVolume: Float, musicVolume: Float) {
        val newSettings = Settings(soundVolume, musicVolume)

        if (!newSettings.isValid()) {
            withView { showError("Invalid settings values") }
            return
        }

        tryToExecute(
            execute = { settingsRepository.saveSettings(newSettings) },
            onSuccess = {
                initialSettings = newSettings
                withView { onSettingsSaved() }
            },
            onError = {
                withView { showError("Failed to save settings") }
            },
            showLoading = true
        )
    }

    fun discardChanges() {
        initialSettings?.let { settings ->
            withView {
                displaySettings(settings)
                onSettingsDiscarded()
            }
        } ?: withView { onSettingsDiscarded() }
    }
}