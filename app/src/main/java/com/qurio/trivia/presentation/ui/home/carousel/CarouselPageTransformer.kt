package com.qurio.trivia.presentation.ui.home.carousel

import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs


fun carouselPageTransformer(): ViewPager2.PageTransformer {


    return ViewPager2.PageTransformer { page, position ->
        val absPosition = abs(position)

        // Scale effect - center card larger
        val scale = 1f - (absPosition * 0.15f).coerceAtMost(0.15f)
        page.scaleX = scale
        page.scaleY = scale

        // Rotation for 3D carousel effect
        page.rotationY = -position * 25f

        // Slight tilt for depth
        page.rotationX = absPosition * 10f

        // Horizontal spacing
        page.translationX = -position * page.width * 0.25f

        // Vertical lift for center card
        page.translationY = absPosition * page.height * 0.08f

        // Fade out distant cards
        page.alpha = when {
            absPosition >= 2f -> 0f
            absPosition > 1f -> 1f - (absPosition - 1f)
            else -> 1f
        }

        // Elevation for depth
        page.translationZ = (1f - absPosition.coerceAtMost(1f)) * 8f

        // 3D perspective
        page.cameraDistance = page.width * 12f
    }
}