package com.qurio.trivia.presentation.ui.home.carousel

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class CarouselConfigurator(
    private val viewPager: ViewPager2,
    private val onPageChanged: () -> Unit
) {

    init {
        setupViewPager()
        setupPageChangeCallback()
    }

    private fun setupViewPager() {
        viewPager.apply {
            offscreenPageLimit = 3
            setPageTransformer(carouselPageTransformer())
            (getChildAt(0) as? RecyclerView)?.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }
    }

    private fun setupPageChangeCallback() {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                onPageChanged()
            }
        })
    }
}