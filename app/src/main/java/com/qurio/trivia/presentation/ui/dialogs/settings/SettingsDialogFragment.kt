package com.qurio.trivia.presentation.ui.dialogs.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.databinding.DialogSettingsBinding
import com.qurio.trivia.domain.model.Settings
import com.qurio.trivia.presentation.base.BaseDialogFragment
import com.qurio.trivia.presentation.ui.dialogs.settings.manager.SettingsUIManager
import javax.inject.Inject

class SettingsDialog : BaseDialogFragment(), SettingsView {

    private var _binding: DialogSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter: SettingsPresenter

    private lateinit var uiManager: SettingsUIManager
    private var onSettingsSavedListener: ((Settings) -> Unit)? = null

    companion object {
        const val TAG = "SettingsDialog"
        fun newInstance() = SettingsDialog()
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
        initializeManagers()
        setupClickListeners()
        presenter.attachView(this)
        presenter.loadSettings()
    }

    private fun initializeManagers() {
        uiManager = SettingsUIManager(binding)
    }

    private fun setupClickListeners() {
        binding.apply {
            btnClose.setOnClickListener { presenter.discardChanges() }
            btnSave.setOnClickListener { saveSettings() }
            btnDiscard.setOnClickListener { presenter.discardChanges() }
        }
    }

    private fun saveSettings() {
        val settings = uiManager.getCurrentSettings()
        presenter.saveSettings(settings.soundVolume, settings.musicVolume)
    }

    override fun displaySettings(settings: Settings) {
        uiManager.displaySettings(settings)
    }

    override fun onSettingsSaved() {
        val settings = uiManager.getCurrentSettings()
        onSettingsSavedListener?.invoke(settings)
        dismiss()
    }

    override fun onSettingsDiscarded() {
        dismiss()
    }

    override fun showLoading() {
        uiManager.setControlsEnabled(false)
    }

    override fun hideLoading() {
        uiManager.setControlsEnabled(true)
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