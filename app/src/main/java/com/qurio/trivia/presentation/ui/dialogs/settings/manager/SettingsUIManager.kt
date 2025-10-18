package com.qurio.trivia.presentation.ui.dialogs.settings.manager

import android.widget.SeekBar
import com.qurio.trivia.databinding.DialogSettingsBinding
import com.qurio.trivia.domain.model.Settings

class SettingsUIManager(
    private val binding: DialogSettingsBinding
) {

    fun setupSeekBarListeners(onVolumeChanged: (soundVolume: Float, musicVolume: Float) -> Unit) {
        binding.seekbarSound.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    onVolumeChanged(progress.toFloat(), binding.seekbarMusic.progress.toFloat())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.seekbarMusic.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    onVolumeChanged(binding.seekbarSound.progress.toFloat(), progress.toFloat())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    fun displaySettings(settings: Settings) {
        binding.seekbarSound.progress = settings.soundVolume.toInt()
        binding.seekbarMusic.progress = settings.musicVolume.toInt()
    }

    fun getCurrentSettings(): Settings {
        return Settings(
            soundVolume = binding.seekbarSound.progress.toFloat(),
            musicVolume = binding.seekbarMusic.progress.toFloat()
        )
    }

    fun setControlsEnabled(enabled: Boolean) {
        binding.apply {
            seekbarSound.isEnabled = enabled
            seekbarMusic.isEnabled = enabled
            btnSave.isEnabled = enabled
            btnDiscard.isEnabled = enabled
        }
    }
}