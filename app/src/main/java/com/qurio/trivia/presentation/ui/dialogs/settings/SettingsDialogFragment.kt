package com.qurio.trivia.presentation.ui.dialogs.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qurio.trivia.QuriοApp
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
        fun newInstance() = SettingsDialogFragment()
    }

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
        setupListeners()
        presenter.attachView(this)
        presenter.loadSettings()
    }

    private fun setupListeners() {
        binding.apply {
            btnClose.setOnClickListener {
                presenter.discardChanges()
            }

            btnSave.setOnClickListener {
                saveSettings()
            }

            btnDiscard.setOnClickListener {
                presenter.discardChanges()
            }
        }
    }

    private fun saveSettings() {
        val soundVolume = binding.seekbarSound.progress.toFloat()
        val musicVolume = binding.seekbarMusic.progress.toFloat()
        presenter.saveSettings(soundVolume, musicVolume)
    }

    override fun displaySettings(settings: Settings) {
        binding.apply {
            seekbarSound.progress = settings.soundVolume.toInt()
            seekbarMusic.progress = settings.musicVolume.toInt()
        }
    }

    override fun onSettingsSaved() {
        val settings = Settings(
            soundVolume = binding.seekbarSound.progress.toFloat(),
            musicVolume = binding.seekbarMusic.progress.toFloat()
        )
        onSettingsSavedListener?.invoke(settings)
        dismiss()
    }

    override fun onSettingsDiscarded() {
        dismiss()
    }

    override fun showLoading() {
        setControlsEnabled(false)
    }

    override fun hideLoading() {
        setControlsEnabled(true)
    }

    private fun setControlsEnabled(enabled: Boolean) {
        binding.apply {
            btnSave.isEnabled = enabled
            btnDiscard.isEnabled = enabled
            seekbarSound.isEnabled = enabled
            seekbarMusic.isEnabled = enabled
        }
    }

    fun setOnSettingsSavedListener(listener: (Settings) -> Unit) {
        onSettingsSavedListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }
}