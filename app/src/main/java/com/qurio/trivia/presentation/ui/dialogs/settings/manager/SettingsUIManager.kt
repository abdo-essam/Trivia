package com.qurio.trivia.presentation.ui.dialogs.settings.manager

import android.widget.SeekBar
import com.qurio.trivia.databinding.DialogSettingsBinding

class SettingsUIManager(
    private val binding: DialogSettingsBinding
) {

    fun setupSeekBarListener(onVolumeChanged: (Float) -> Unit) {
        binding.seekbarSound.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    onVolumeChanged(progress.toFloat())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    fun displayVolume(volume: Float) {
        binding.seekbarSound.progress = volume.toInt()
    }

    fun getCurrentVolume(): Float {
        return binding.seekbarSound.progress.toFloat()
    }

    fun setControlsEnabled(enabled: Boolean) {
        binding.apply {
            seekbarSound.isEnabled = enabled
            btnSave.isEnabled = enabled
            btnDiscard.isEnabled = enabled
        }
    }
}