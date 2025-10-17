package com.qurio.trivia.presentation.ui.onboarding.handlers

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.qurio.trivia.databinding.FragmentOnboardingBinding

class OnboardingNavigationHandler(
    private val binding: FragmentOnboardingBinding,
    private val viewPager: ViewPager2,
    private val totalPages: Int
) {
    private var currentPage = 0

    init {
        setupNavigation()
        setupPageChangeListener()
    }

    private fun setupNavigation() {
        binding.btnPrevious.setOnClickListener {
            navigateToPrevious()
        }

        binding.btnNext.setOnClickListener {
            navigateToNext()
        }
    }

    private fun setupPageChangeListener() {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
                updateNavigationVisibility()
            }
        })
    }

    private fun navigateToPrevious() {
        if (currentPage > 0) {
            animateButton(binding.btnPrevious) {
                viewPager.currentItem = currentPage - 1
            }
        }
    }

    private fun navigateToNext() {
        if (currentPage < totalPages - 1) {
            animateButton(binding.btnNext) {
                viewPager.currentItem = currentPage + 1
            }
        }
    }

    private fun updateNavigationVisibility() {
        binding.btnPrevious.alpha = if (currentPage > 0) ALPHA_ENABLED else ALPHA_DISABLED
        binding.btnNext.alpha = if (currentPage < totalPages - 1) ALPHA_ENABLED else ALPHA_DISABLED

        binding.iconPrevious.alpha = if (currentPage > 0) ALPHA_ENABLED else ALPHA_DISABLED
        binding.iconNext.alpha = if (currentPage < totalPages - 1) ALPHA_ENABLED else ALPHA_DISABLED
    }

    private fun animateButton(button: View, onComplete: () -> Unit) {
        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            button,
            PropertyValuesHolder.ofFloat(SCALE_X, 1f, 0.95f),
            PropertyValuesHolder.ofFloat(SCALE_Y, 1f, 0.95f)
        ).apply {
            duration = ANIMATION_DURATION
        }

        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
            button,
            PropertyValuesHolder.ofFloat(SCALE_X, 0.95f, 1f),
            PropertyValuesHolder.ofFloat(SCALE_Y, 0.95f, 1f)
        ).apply {
            duration = ANIMATION_DURATION
            startDelay = ANIMATION_DURATION
        }

        scaleDown.start()
        scaleUp.start()

        button.postDelayed(onComplete, ANIMATION_DURATION * 2)
    }

    fun cleanup() {
        // Remove any callbacks if needed
    }

    companion object {
        private const val ALPHA_ENABLED = 1f
        private const val ALPHA_DISABLED = 0.3f
        private const val ANIMATION_DURATION = 100L
        private const val SCALE_X = "scaleX"
        private const val SCALE_Y = "scaleY"
    }
}