package com.qurio.trivia.presentation.ui.onboarding.handlers

import android.annotation.SuppressLint
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import com.qurio.trivia.R
import com.qurio.trivia.databinding.LayoutSwipeUpBinding
import kotlin.math.abs

class SwipeUpGestureHandler(
    private val fragment: Fragment,
    private val binding: LayoutSwipeUpBinding,
    private val onSwipeComplete: () -> Unit
) {
    private var initialY = 0f
    private var initialAvatarY = 0f
    private var isDragging = false

    init {
        setupDragGesture()
        startArrowAnimations()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDragGesture() {
        binding.swipeUpContainer.setOnTouchListener { _, event ->
            handleDragTouch(event)
        }
    }

    private fun handleDragTouch(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> handleDragStart(event)
            MotionEvent.ACTION_MOVE -> handleDragMove(event)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> handleDragEnd(event)
            else -> false
        }
    }

    private fun handleDragStart(event: MotionEvent): Boolean {
        initialY = event.rawY
        initialAvatarY = binding.avatarContainer.translationY
        isDragging = true

        binding.avatarContainer.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

        // Scale up avatar
        binding.avatarContainer.animate()
            .scaleX(AVATAR_SCALE_UP)
            .scaleY(AVATAR_SCALE_UP)
            .setDuration(AVATAR_SCALE_DURATION)
            .start()

        // Pause arrow animations
        pauseArrowAnimations()

        return true
    }

    private fun handleDragMove(event: MotionEvent): Boolean {
        if (!isDragging) return false

        val deltaY = event.rawY - initialY

        // Only allow upward movement
        if (deltaY < 0) {
            val translation = deltaY * DRAG_RESISTANCE
            binding.avatarContainer.translationY = initialAvatarY + translation

            // Rotate avatar as it moves
            val rotation = (translation / DRAG_THRESHOLD) * -15f
            binding.avatarContainer.rotation = rotation

            // Update alpha based on drag distance
            val progress = abs(translation) / DRAG_THRESHOLD
            binding.avatarContainer.alpha = 1f - (progress * 0.2f)
            binding.tvSwipeText.alpha = 1f - progress
        }

        return true
    }

    private fun handleDragEnd(event: MotionEvent): Boolean {
        if (!isDragging) return false

        isDragging = false
        val deltaY = event.rawY - initialY

        if (deltaY < -DRAG_THRESHOLD) {
            animateAvatarAndComplete()
        } else {
            animateAvatarBack()
            startArrowAnimations()
        }

        return true
    }

    private fun animateAvatarAndComplete() {
        // Animate avatar flying up
        binding.avatarContainer.animate()
            .translationY(-binding.root.height.toFloat())
            .alpha(0f)
            .scaleX(0.5f)
            .scaleY(0.5f)
            .rotation(-30f)
            .setDuration(AVATAR_FLY_DURATION)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                onSwipeComplete()
            }
            .start()

        // Fade out other elements
        fadeOutElements()
    }

    private fun animateAvatarBack() {
        binding.avatarContainer.animate()
            .translationY(0f)
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .rotation(0f)
            .setDuration(AVATAR_BACK_DURATION)
            .setInterpolator(OvershootInterpolator(2f))
            .start()

        binding.tvSwipeText.animate()
            .alpha(1f)
            .setDuration(TEXT_FADE_DURATION)
            .start()
    }

    private fun fadeOutElements() {
        binding.tvSwipeText.animate()
            .alpha(0f)
            .setDuration(FADE_OUT_DURATION)
            .start()

        binding.arrow1.animate().alpha(0f).setDuration(FADE_OUT_DURATION).start()
        binding.arrow2.animate().alpha(0f).setDuration(FADE_OUT_DURATION).start()
        binding.arrow3.animate().alpha(0f).setDuration(FADE_OUT_DURATION).start()
    }

    private fun startArrowAnimations() {
        if (!fragment.isAdded) return

        val animation = AnimationUtils.loadAnimation(fragment.requireContext(), R.anim.arrow_animation)

        binding.arrow1.startAnimation(animation)

        binding.arrow2.postDelayed({
            if (fragment.isAdded) {
                binding.arrow2.startAnimation(
                    AnimationUtils.loadAnimation(fragment.requireContext(), R.anim.arrow_animation)
                )
            }
        }, ARROW_DELAY_SHORT)

        binding.arrow3.postDelayed({
            if (fragment.isAdded) {
                binding.arrow3.startAnimation(
                    AnimationUtils.loadAnimation(fragment.requireContext(), R.anim.arrow_animation)
                )
            }
        }, ARROW_DELAY_LONG)
    }

    private fun pauseArrowAnimations() {
        binding.arrow1.clearAnimation()
        binding.arrow2.clearAnimation()
        binding.arrow3.clearAnimation()
    }

    fun cleanup() {
        pauseArrowAnimations()
    }

    companion object {
        private const val DRAG_THRESHOLD = 200f
        private const val DRAG_RESISTANCE = 0.8f
        private const val AVATAR_SCALE_UP = 1.1f
        private const val AVATAR_SCALE_DURATION = 100L
        private const val AVATAR_FLY_DURATION = 400L
        private const val AVATAR_BACK_DURATION = 400L
        private const val FADE_OUT_DURATION = 200L
        private const val TEXT_FADE_DURATION = 300L
        private const val ARROW_DELAY_SHORT = 200L
        private const val ARROW_DELAY_LONG = 400L
    }
}