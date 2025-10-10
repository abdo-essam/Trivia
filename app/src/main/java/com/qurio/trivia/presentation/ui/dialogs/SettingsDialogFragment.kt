package com.qurio.trivia.presentation.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qurio.trivia.databinding.DialogSettingsBinding

class SettingsDialogFragment : BaseDialogFragment() {

    private var _binding: DialogSettingsBinding? = null
    private val binding get() = _binding!!

    private var initialSoundValue = 80f
    private var initialMusicValue = 60f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        loadSettings()
    }

    private fun setupViews() {
        binding.apply {
            btnClose.setOnClickListener { dismiss() }

            btnSave.setOnClickListener {
                saveSettings()
                dismiss()
            }

            btnDiscard.setOnClickListener {
                restoreSettings()
                dismiss()
            }
        }
    }

    private fun loadSettings() {
        // TODO: Load from SharedPreferences or ViewModel
        binding.sliderSound.value = initialSoundValue
        binding.sliderMusic.value = initialMusicValue
    }

    private fun saveSettings() {
        val soundValue = binding.sliderSound.value
        val musicValue = binding.sliderMusic.value

        // TODO: Save to SharedPreferences or ViewModel
        // Also update sound and music managers
    }

    private fun restoreSettings() {
        binding.sliderSound.value = initialSoundValue
        binding.sliderMusic.value = initialMusicValue
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SettingsDialogFragment"
    }
}