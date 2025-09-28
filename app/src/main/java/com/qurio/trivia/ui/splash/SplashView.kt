package com.qurio.trivia.ui.splash

import com.qurio.trivia.base.BaseView

interface SplashView : BaseView {
    fun navigateToOnboarding()
    fun navigateToHome()
}