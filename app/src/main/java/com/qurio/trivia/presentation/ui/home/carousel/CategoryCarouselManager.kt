package com.qurio.trivia.presentation.ui.home.carousel

import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.*

/**
 * Manages auto-scrolling behavior for category carousel
 */
class CategoryCarouselManager(
    private val viewPager: ViewPager2,
    private val scrollIntervalMs: Long = 3000L,
    private val scrollDurationMs: Int = 600
) {

    private var autoScrollJob: Job? = null
    private val recyclerView: RecyclerView? = viewPager.getChildAt(0) as? RecyclerView

    /**
     * Start automatic scrolling through categories
     */
    fun startAutoScroll(itemCount: Int, coroutineScope: CoroutineScope) {
        if (itemCount <= 1) return

        stopAutoScroll()

        autoScrollJob = coroutineScope.launch {
            var currentPosition = 0

            while (isActive) {
                delay(scrollIntervalMs)

                currentPosition = (currentPosition + 1) % itemCount

                smoothScrollToPosition(currentPosition)
            }
        }
    }

    /**
     * Stop automatic scrolling
     */
    fun stopAutoScroll() {
        autoScrollJob?.cancel()
        autoScrollJob = null
    }

    /**
     * Smooth scroll to specific position
     */
    private fun smoothScrollToPosition(position: Int) {
        recyclerView?.let { rv ->
            val smoothScroller = object : LinearSmoothScroller(viewPager.context) {
                override fun getHorizontalSnapPreference(): Int = SNAP_TO_START

                override fun calculateTimeForScrolling(dx: Int): Int {
                    return scrollDurationMs.coerceAtMost(super.calculateTimeForScrolling(dx))
                }
            }

            smoothScroller.targetPosition = position
            rv.layoutManager?.startSmoothScroll(smoothScroller)
        }
    }

    /**
     * Pause auto-scroll (useful when user interacts)
     */
    fun pause() {
        stopAutoScroll()
    }

    /**
     * Resume auto-scroll
     */
    fun resume(itemCount: Int, coroutineScope: CoroutineScope) {
        startAutoScroll(itemCount, coroutineScope)
    }
}