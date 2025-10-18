package com.qurio.trivia.presentation.ui.home.carousel

import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

fun carouselPageTransformer(): ViewPager2.PageTransformer {
    return ViewPager2.PageTransformer { page, position ->
        val absPosition = abs(position)

        // Scale effect - center card slightly larger
        val scale = 1f - (absPosition * 0.1f).coerceAtMost(0.15f)
        page.scaleX = scale
        page.scaleY = scale

        // Rotation - card tilts as it moves to sides
        page.rotation = -position * 15f  // Adjust value for more/less tilt

        // Set pivot point for natural rotation
        page.pivotX = page.width / 2f
        page.pivotY = page.height / 2f

        // Horizontal spacing
        page.translationX = -position * page.width * 0.25f

        // Vertical lift for center card
        page.translationY = absPosition * page.height * 0.05f

        // Fade out distant cards
        page.alpha = when {
            absPosition >= 2f -> 0f
            absPosition > 1f -> 1f - (absPosition - 1f)
            else -> 1f
        }

        // Elevation for depth
        page.translationZ = (1f - absPosition.coerceAtMost(1f)) * 8f
    }
}