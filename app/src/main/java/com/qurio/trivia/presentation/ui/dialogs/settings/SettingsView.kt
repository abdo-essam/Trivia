package com.qurio.trivia.presentation.ui.dialogs.settings

import com.qurio.trivia.domain.model.Settings
import com.qurio.trivia.presentation.base.BaseView

interface SettingsView : BaseView {
    fun displaySettings(settings: Settings)
    fun onSettingsSaved()
}