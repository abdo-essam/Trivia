package com.qurio.trivia.presentation.ui.home.carousel

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

/**
 * Page transformer for category carousel effect
 * Handles scaling, elevation, rotation, and positioning of cards
 */
class CarouselPageTransformer(
    private val pageMarginPx: Int,
    private val extraLiftPx: Int
) : ViewPager2.PageTransformer {

    companion object {
        private const val MIN_SCALE = 0.9f
        private const val SCALE_FACTOR = 0.1f
        private const val CENTER_THRESHOLD = 0.5f
        private const val ELEVATION_CENTER = 8f
        private const val ELEVATION_SIDE = 0f
        private const val MAX_ROTATION = -4f // Maximum rotation angle in degrees
    }

    override fun transformPage(page: View, position: Float) {
        val absPosition = abs(position)

        page.apply {
            // Horizontal positioning - controls card spacing
            translationX = -pageMarginPx * position

            // Scale animation - center card is full size, side cards slightly smaller
            val scale = (MIN_SCALE + (1f - absPosition.coerceAtMost(1f)) * SCALE_FACTOR)
            scaleX = scale
            scaleY = scale

            // Rotation animation - smooth gradual rotation
            // Negative position (left) = negative rotation (tilt left)
            // Positive position (right) = positive rotation (tilt right)
            rotation = -position * MAX_ROTATION

            // Set pivot point for natural rotation around center
            pivotX = width / 2f
            pivotY = height / 2f

            // Vertical elevation - lift center card
            if (absPosition < CENTER_THRESHOLD) {
                translationY = -extraLiftPx.toFloat()
                translationZ = ELEVATION_CENTER
            } else {
                translationY = ELEVATION_SIDE
                translationZ = ELEVATION_SIDE
            }
        }
    }
}