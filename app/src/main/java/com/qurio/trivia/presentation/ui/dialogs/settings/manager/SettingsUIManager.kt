package com.qurio.trivia.presentation.ui.dialogs.settings.manager

import android.widget.SeekBar
import com.qurio.trivia.databinding.DialogSettingsBinding
import com.qurio.trivia.domain.model.Settings

class SettingsUIManager(
    private val binding: DialogSettingsBinding
) {
    private var onVolumeChangedListener: ((soundVolume: Float, musicVolume: Float) -> Unit)? = null

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

    fun setupSeekBarListeners(onVolumeChanged: (soundVolume: Float, musicVolume: Float) -> Unit) {
        onVolumeChangedListener = onVolumeChanged

        binding.seekbarSound.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    notifyVolumeChanged()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.seekbarMusic.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    notifyVolumeChanged()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun notifyVolumeChanged() {
        val soundVolume = binding.seekbarSound.progress.toFloat()
        val musicVolume = binding.seekbarMusic.progress.toFloat()
        onVolumeChangedListener?.invoke(soundVolume, musicVolume)
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