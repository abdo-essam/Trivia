package com.qurio.trivia.presentation.ui.dialogs.settings

import com.qurio.trivia.presentation.base.BaseView

interface SettingsView : BaseView {
    fun displayVolume(volume: Float)
    fun onSettingsSaved()
}