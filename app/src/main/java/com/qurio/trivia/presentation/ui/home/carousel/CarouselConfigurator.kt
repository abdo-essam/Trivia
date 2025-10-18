package com.qurio.trivia.presentation.ui.home.carousel

import android.content.res.Resources
import androidx.lifecycle.LifecycleObserver
import androidx.viewpager2.widget.ViewPager2
import com.qurio.trivia.R
import kotlinx.coroutines.CoroutineScope

/**
 * Configures and manages the category carousel ViewPager2
 */
class CarouselConfigurator(
    private val viewPager: ViewPager2,
    private val resources: Resources,
    private val coroutineScope: CoroutineScope
) : LifecycleObserver {

    private val carouselManager: CategoryCarouselManager = CategoryCarouselManager(
        viewPager = viewPager,
        scrollIntervalMs = 3000L,
        scrollDurationMs = 600
    )
    private var itemCount: Int = 0

    init {
        setupViewPager()
    }

    /**
     * Setup ViewPager2 with transformer and callbacks
     */
    private fun setupViewPager() {
        val pageMargin = resources.getDimensionPixelOffset(R.dimen.page_offset)
        val extraLift = resources.getDimensionPixelOffset(R.dimen.page_margin)

        viewPager.apply {
            // Show multiple pages at once
            offscreenPageLimit = 3

            // Apply carousel transformer
            setPageTransformer(CarouselPageTransformer(pageMargin, extraLift))

            // Pause auto-scroll when user interacts
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                private var isDragging = false

                override fun onPageScrollStateChanged(state: Int) {
                    when (state) {
                        ViewPager2.SCROLL_STATE_DRAGGING -> {
                            isDragging = true
                            carouselManager.pause()
                        }
                        ViewPager2.SCROLL_STATE_IDLE -> {
                            if (isDragging) {
                                isDragging = false
                                // Resume after user stops interacting
                                postDelayed({
                                    carouselManager.resume(itemCount, coroutineScope)
                                }, 2000)
                            }
                        }
                    }
                }
            })
        }
    }

    /**
     * Start carousel with given item count
     */
    fun start(count: Int) {
        itemCount = count
        carouselManager.startAutoScroll(count, coroutineScope)
    }

    /**
     * Stop carousel
     */
    fun stop() {
        carouselManager.stopAutoScroll()
    }

}