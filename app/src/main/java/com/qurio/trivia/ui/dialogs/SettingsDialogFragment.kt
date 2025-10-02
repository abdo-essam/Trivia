package com.qurio.trivia.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.qurio.trivia.R
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.databinding.DialogSettingsBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsDialogFragment : DialogFragment() {

    @Inject
    lateinit var userProgressDao: UserProgressDao

    private lateinit var binding: DialogSettingsBinding
    private var soundEnabled = true
    private var musicEnabled = true

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        (requireActivity().application as QuriοApp).appComponent.inject(this)

        binding = DialogSettingsBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext(), R.style.SettingsDialogTheme)
        dialog.setContentView(binding.root)

        setupViews()
        loadCurrentSettings()

        return dialog
    }

    private fun setupViews() {
        binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
            soundEnabled = isChecked
        }

        binding.switchMusic.setOnCheckedChangeListener { _, isChecked ->
            musicEnabled = isChecked
        }

        binding.btnSaveChanges.setOnClickListener {
            saveSettings()
        }

        binding.btnDiscard.setOnClickListener {
            dismiss()
        }
    }

    private fun loadCurrentSettings() {
        lifecycleScope.launch {
            val userProgress = userProgressDao.getUserProgress()
            userProgress?.let {
                soundEnabled = it.soundEnabled
                musicEnabled = it.musicEnabled

                binding.switchSound.isChecked = soundEnabled
                binding.switchMusic.isChecked = musicEnabled
            }
        }
    }

    private fun saveSettings() {
        lifecycleScope.launch {
            userProgressDao.updateSoundEnabled(soundEnabled)
            userProgressDao.updateMusicEnabled(musicEnabled)
            dismiss()
        }
    }
}