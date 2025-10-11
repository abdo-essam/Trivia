package com.qurio.trivia.presentation.ui.dialogs.settings

import android.util.Log
import com.qurio.trivia.domain.model.Settings
import com.qurio.trivia.domain.repository.SettingsRepository
import com.qurio.trivia.presentation.base.BasePresenter
import javax.inject.Inject

class SettingsPresenter @Inject constructor(
    private val settingsRepository: SettingsRepository
) : BasePresenter<SettingsView>() {

    companion object {
        private const val TAG = "SettingsPresenter"
    }

    private var initialSettings: Settings? = null

    // ========== Load Settings ==========

    fun loadSettings() {
        tryToExecute(
            execute = {
                settingsRepository.getSettings()
            },
            onSuccess = { settings ->
                Log.d(TAG, "Settings loaded: sound=${settings.soundVolume}, music=${settings.musicVolume}")
                initialSettings = settings
                withView { displaySettings(settings) }
            },
            onError = { error ->
                Log.e(TAG, "Error loading settings", error)
                withView {
                    showError("Failed to load settings")
                    // Load defaults on error
                    displaySettings(Settings.DEFAULT)
                }
            },
            showLoading = false
        )
    }

    // ========== Save Settings ==========

    fun saveSettings(soundVolume: Float, musicVolume: Float) {
        val newSettings = Settings(soundVolume, musicVolume)

        if (!newSettings.isValid()) {
            withView { showError("Invalid settings values") }
            return
        }

        tryToExecute(
            execute = {
                settingsRepository.saveSettings(newSettings)
            },
            onSuccess = {
                Log.d(TAG, "Settings saved successfully")
                initialSettings = newSettings
                withView { onSettingsSaved() }
            },
            onError = { error ->
                Log.e(TAG, "Error saving settings", error)
                withView { showError("Failed to save settings") }
            },
            showLoading = true
        )
    }

    // ========== Discard Changes ==========

    fun discardChanges() {
        initialSettings?.let { settings ->
            Log.d(TAG, "Discarding changes, restoring to: $settings")
            withView {
                displaySettings(settings)
                onSettingsDiscarded()
            }
        }
    }

    // ========== Reset to Defaults ==========

    fun resetToDefaults() {
        tryToExecute(
            execute = {
                settingsRepository.resetToDefaults()
            },
            onSuccess = { settings ->
                Log.d(TAG, "Settings reset to defaults")
                initialSettings = settings
                withView { displaySettings(settings) }
            },
            onError = { error ->
                Log.e(TAG, "Error resetting settings", error)
                withView { showError("Failed to reset settings") }
            },
            showLoading = true
        )
    }
}