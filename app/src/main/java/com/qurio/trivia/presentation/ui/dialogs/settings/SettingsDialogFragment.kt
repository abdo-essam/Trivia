package com.qurio.trivia.presentation.ui.dialogs.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.databinding.DialogSettingsBinding
import com.qurio.trivia.domain.model.Settings
import com.qurio.trivia.presentation.base.BaseDialogFragment
import javax.inject.Inject

class SettingsDialogFragment : BaseDialogFragment(), SettingsView {

    private var _binding: DialogSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter: SettingsPresenter

    private var onSettingsSavedListener: ((Settings) -> Unit)? = null

    companion object {
        const val TAG = "SettingsDialogFragment"

        fun newInstance(): SettingsDialogFragment {
            return SettingsDialogFragment()
        }
    }

    // ========== Lifecycle ==========

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as QuriοApp).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        setupClickListeners()
        loadSettings()
    }

    // ========== Setup Methods ==========

    private fun setupClickListeners() {
        binding.apply {
            btnClose.setOnClickListener {
                Log.d(TAG, "Close button clicked")
                presenter.discardChanges()
            }

            btnSave.setOnClickListener {
                Log.d(TAG, "Save button clicked")
                handleSaveSettings()
            }

            btnDiscard.setOnClickListener {
                Log.d(TAG, "Discard button clicked")
                presenter.discardChanges()
            }
        }
    }

    private fun loadSettings() {
        presenter.attachView(this)
        presenter.loadSettings()
    }

    // ========== Save Logic ==========

    private fun handleSaveSettings() {
        val soundVolume = binding.sliderSound.value
        val musicVolume = binding.sliderMusic.value

        Log.d(TAG, "Saving settings: sound=$soundVolume, music=$musicVolume")
        presenter.saveSettings(soundVolume, musicVolume)
    }

    // ========== SettingsView Implementation ==========

    override fun displaySettings(settings: Settings) {
        Log.d(TAG, "Displaying settings: $settings")

        binding.apply {
            sliderSound.value = settings.soundVolume
            sliderMusic.value = settings.musicVolume
        }
    }

    override fun onSettingsSaved() {
        Log.d(TAG, "Settings saved successfully")

        val savedSettings = Settings(
            soundVolume = binding.sliderSound.value,
            musicVolume = binding.sliderMusic.value
        )

        //showMessage(getString(R.string.settings_saved))
        onSettingsSavedListener?.invoke(savedSettings)

        dismiss()
    }

    override fun onSettingsDiscarded() {
        Log.d(TAG, "Settings discarded")
        //showMessage(getString(R.string.settings_discarded))
        dismiss()
    }

    override fun showLoading() {
        binding.apply {
            btnSave.isEnabled = false
            btnDiscard.isEnabled = false
            sliderSound.isEnabled = false
            sliderMusic.isEnabled = false
        }
    }

    override fun hideLoading() {
        binding.apply {
            btnSave.isEnabled = true
            btnDiscard.isEnabled = true
            sliderSound.isEnabled = true
            sliderMusic.isEnabled = true
        }
    }

    // ========== Listener ==========

    fun setOnSettingsSavedListener(listener: (Settings) -> Unit) {
        onSettingsSavedListener = listener
    }

    // ========== Lifecycle ==========

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }
}