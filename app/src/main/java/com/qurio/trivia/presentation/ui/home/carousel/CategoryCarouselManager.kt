package com.qurio.trivia.presentation.ui.home.carousel

import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.*

class CategoryCarouselManager(
    private val viewPager: ViewPager2
) {
    private var autoScrollJob: Job? = null

    fun startAutoScroll(itemCount: Int, coroutineScope: CoroutineScope) {
        if (itemCount <= 1) return

        stopAutoScroll()

        autoScrollJob = coroutineScope.launch {
            var currentPosition = viewPager.currentItem

            while (isActive) {
                delay(3000)
                currentPosition = (currentPosition + 1) % itemCount

                withContext(Dispatchers.Main) {
                    viewPager.setCurrentItem(currentPosition, true)
                }
            }
        }
    }

    fun stopAutoScroll() {
        autoScrollJob?.cancel()
        autoScrollJob = null
    }

    fun pause() = stopAutoScroll()

    fun resume(itemCount: Int, coroutineScope: CoroutineScope) {
        startAutoScroll(itemCount, coroutineScope)
    }
}