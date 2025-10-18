package com.qurio.trivia.presentation.ui.dialogs.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qurio.trivia.QurioApp
import com.qurio.trivia.databinding.DialogSettingsBinding
import com.qurio.trivia.presentation.base.BaseDialogFragment
import com.qurio.trivia.presentation.ui.dialogs.settings.manager.SettingsUIManager
import com.qurio.trivia.utils.Constants
import javax.inject.Inject

class SettingsDialog : BaseDialogFragment(), SettingsView {

    private var _binding: DialogSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter: SettingsPresenter

    private lateinit var uiManager: SettingsUIManager
    private var onSettingsSavedListener: ((Float) -> Unit)? = null

    private var initialVolume: Float? = null

    companion object {
        fun newInstance() = SettingsDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as QurioApp).appComponent.inject(this)
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
        initializeManagers()
        setupClickListeners()
        setupSeekBarListener()
        presenter.attachView(this)
        presenter.loadSettings()
    }

    private fun initializeManagers() {
        uiManager = SettingsUIManager(binding)
    }

    private fun setupClickListeners() {
        binding.apply {
            btnClose.setOnClickListener { discardChanges() }
            btnSave.setOnClickListener { saveSettings() }
            btnDiscard.setOnClickListener { discardChanges() }
        }
    }

    private fun setupSeekBarListener() {
        uiManager.setupSeekBarListener { volume ->
            presenter.updateVolumePreview(volume)
        }
    }

    private fun saveSettings() {
        val volume = uiManager.getCurrentVolume()
        presenter.saveSettings(volume)
    }

    private fun discardChanges() {
        initialVolume?.let { volume ->
            presenter.restoreVolume(volume)
        }
        dismiss()
    }

    override fun displayVolume(volume: Float) {
        if (initialVolume == null) {
            initialVolume = volume
        }
        uiManager.displayVolume(volume)
    }

    override fun onSettingsSaved() {
        val volume = uiManager.getCurrentVolume()
        onSettingsSavedListener?.invoke(volume)
        dismiss()
    }

    override fun showLoading() {
        uiManager.setControlsEnabled(false)
    }

    override fun hideLoading() {
        uiManager.setControlsEnabled(true)
    }

    fun setOnSettingsSavedListener(listener: (Float) -> Unit) {
        onSettingsSavedListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }
}