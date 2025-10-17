package com.qurio.trivia.presentation.ui.game.managers

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.qurio.trivia.R
import kotlin.random.Random

class GameAnimationManager(
    private val floatingTagsContainer: FrameLayout,
    private val tagSize: Int
) {

    fun animateLifeLoss(livesView: View) {
        val shake = ObjectAnimator.ofFloat(
            livesView,
            View.TRANSLATION_X,
            0f, -25f, 25f, -25f, 25f, -15f, 15f, -5f, 5f, 0f
        ).apply {
            duration = SHAKE_DURATION
        }

        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.3f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.3f, 1f)
        val scale = ObjectAnimator.ofPropertyValuesHolder(livesView, scaleX, scaleY).apply {
            duration = SCALE_DURATION
        }

        shake.start()
        scale.start()
    }

    fun showFloatingTags(isCorrect: Boolean) {
        val tagCount = Random.nextInt(MIN_TAGS, MAX_TAGS + 1)
        repeat(tagCount) {
            createFloatingTag(isCorrect)
        }
    }

    private fun createFloatingTag(isCorrect: Boolean) {
        val imageView = ImageView(floatingTagsContainer.context).apply {
            setImageResource(
                if (isCorrect) R.drawable.ic_floating_tag_correct
                else R.drawable.ic_floating_tag_incorrect
            )
            layoutParams = FrameLayout.LayoutParams(tagSize, tagSize)
            alpha = 0f
        }

        positionTagRandomly(imageView)
        floatingTagsContainer.addView(imageView)
        animateFloatingTag(imageView)
    }

    private fun positionTagRandomly(view: View) {
        val containerWidth = floatingTagsContainer.width
        val containerHeight = floatingTagsContainer.height

        if (containerWidth <= 0 || containerHeight <= 0) return

        val params = view.layoutParams as FrameLayout.LayoutParams

        params.leftMargin = Random.nextInt(
            TAG_MARGIN_MIN,
            maxOf(containerWidth - TAG_MARGIN_MAX, TAG_MARGIN_MIN + 1)
        )
        params.topMargin = Random.nextInt(
            TAG_TOP_MARGIN,
            maxOf(containerHeight / 2, TAG_TOP_MARGIN + 1)
        )

        view.layoutParams = params
    }

    private fun animateFloatingTag(view: View) {
        val startDelay = Random.nextLong(0, TAG_ANIMATION_DELAY_MAX)

        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
        val translateY = PropertyValuesHolder.ofFloat(
            View.TRANSLATION_Y,
            0f,
            -TAG_TRANSLATION_Y
        )

        ObjectAnimator.ofPropertyValuesHolder(view, alpha, translateY).apply {
            duration = TAG_ANIMATION_DURATION
            this.startDelay = startDelay

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    fadeOutAndRemoveTag(view)
                }
            })

            start()
        }
    }

    private fun fadeOutAndRemoveTag(view: View) {
        view.animate()
            .alpha(0f)
            .setDuration(TAG_FADE_DURATION)
            .setStartDelay(TAG_FADE_DELAY)
            .withEndAction {
                (view.parent as? ViewGroup)?.removeView(view)
            }
            .start()
    }

    fun clearFloatingTags() {
        floatingTagsContainer.removeAllViews()
    }

    companion object {
        private const val SHAKE_DURATION = 500L
        private const val SCALE_DURATION = 300L
        private const val MIN_TAGS = 5
        private const val MAX_TAGS = 8
        private const val TAG_MARGIN_MIN = 50
        private const val TAG_MARGIN_MAX = 150
        private const val TAG_TOP_MARGIN = 100
        private const val TAG_TRANSLATION_Y = 150f
        private const val TAG_ANIMATION_DURATION = 1000L
        private const val TAG_ANIMATION_DELAY_MAX = 300L
        private const val TAG_FADE_DURATION = 400L
        private const val TAG_FADE_DELAY = 300L
    }
}