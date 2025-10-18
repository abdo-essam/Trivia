package com.qurio.trivia.presentation.ui.home.carousel

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class CarouselConfigurator(
    private val viewPager: ViewPager2
) {

    init {
        setupViewPager()
    }

    private fun setupViewPager() {


        viewPager.apply {
            offscreenPageLimit = 3

            setPageTransformer(carouselPageTransformer())

            // Smooth scrolling
            (getChildAt(0) as? RecyclerView)?.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }
    }
}