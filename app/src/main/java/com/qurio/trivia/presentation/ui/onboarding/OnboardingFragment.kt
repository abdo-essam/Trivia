package com.qurio.trivia.presentation.ui.onboarding

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.GestureDetector
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import androidx.core.view.GestureDetectorCompat
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.presentation.base.BaseFragment
import com.qurio.trivia.databinding.FragmentOnboardingBinding
import com.qurio.trivia.presentation.adapters.OnboardingAdapter
import javax.inject.Inject
import kotlin.math.abs

class OnboardingFragment : BaseFragment<FragmentOnboardingBinding, OnboardingView, OnboardingPresenter>(),
    OnboardingView {

    @Inject
    lateinit var onboardingPresenter: OnboardingPresenter

    // ========== UI Components ==========

    private lateinit var onboardingAdapter: OnboardingAdapter
    private lateinit var gestureDetector: GestureDetectorCompat

    // ========== State Variables ==========

    private var currentPage = 0
    private var initialY = 0f
    private var isDragging = false
    private var initialAvatarY = 0f

    // ========== BaseFragment Implementation ==========

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as QuriοApp).appComponent.inject(this)
    }

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnboardingBinding {
        return FragmentOnboardingBinding.inflate(inflater, container, false)
    }

    override fun initPresenter(): OnboardingPresenter = onboardingPresenter

    override fun setupViews() {
        setupViewPager()
        setupArrows()
        setupSwipeGesture()
        setupDragGesture()
        startArrowAnimations()
    }

    // ========== ViewPager Setup ==========

    private fun setupViewPager() {
        val onboardingItems = createOnboardingItems()

        onboardingAdapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.apply {
            adapter = onboardingAdapter
            offscreenPageLimit = 1
            registerOnPageChangeCallback(pageChangeCallback)
        }

        updateArrowVisibility()
    }

    private fun createOnboardingItems(): List<OnboardingItem> {
        return listOf(
            OnboardingItem(
                imageRes = R.drawable.onboarding_1,
                title = getString(R.string.onboarding_welcome_title),
                description = getString(R.string.onboarding_welcome_description)
            ),
            OnboardingItem(
                imageRes = R.drawable.onboarding_2,
                title = getString(R.string.onboarding_character_title),
                description = getString(R.string.onboarding_character_description)
            ),
            OnboardingItem(
                imageRes = R.drawable.onboarding_3,
                title = getString(R.string.onboarding_challenge_title),
                description = getString(R.string.onboarding_challenge_description)
            ),
            OnboardingItem(
                imageRes = R.drawable.onboarding_4,
                title = getString(R.string.onboarding_collect_title),
                description = getString(R.string.onboarding_collect_description)
            )
        )
    }

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            currentPage = position
            updateArrowVisibility()
        }
    }

    // ========== Arrow Navigation Setup ==========

    private fun setupArrows() {
        binding.arrowLeftContainer.setOnClickListener {
            navigateToPreviousPage()
        }

        binding.arrowRightContainer.setOnClickListener {
            navigateToNextPage()
        }
    }

    private fun navigateToPreviousPage() {
        if (currentPage > 0) {
            animateArrowPress(binding.arrowLeftContainer) {
                binding.viewPager.currentItem = currentPage - 1
            }
        }
    }

    private fun navigateToNextPage() {
        if (currentPage < TOTAL_PAGES - 1) {
            animateArrowPress(binding.arrowRightContainer) {
                binding.viewPager.currentItem = currentPage + 1
            }
        }
    }

    private fun updateArrowVisibility() {
        binding.arrowLeftContainer.alpha = if (currentPage > 0) ALPHA_ENABLED else ALPHA_DISABLED
        binding.arrowRightContainer.alpha = if (currentPage < TOTAL_PAGES - 1) ALPHA_ENABLED else ALPHA_DISABLED
    }

    private fun animateArrowPress(arrow: View, onComplete: () -> Unit) {
        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            arrow,
            PropertyValuesHolder.ofFloat(SCALE_X, 1f, 0.8f),
            PropertyValuesHolder.ofFloat(SCALE_Y, 1f, 0.8f)
        ).apply {
            duration = ARROW_ANIMATION_DURATION
        }

        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
            arrow,
            PropertyValuesHolder.ofFloat(SCALE_X, 0.8f, 1f),
            PropertyValuesHolder.ofFloat(SCALE_Y, 0.8f, 1f)
        ).apply {
            duration = ARROW_ANIMATION_DURATION
            startDelay = ARROW_ANIMATION_DURATION
        }

        scaleDown.start()
        scaleUp.start()

        arrow.postDelayed(onComplete, ARROW_ANIMATION_TOTAL_DURATION)
    }

    // ========== Drag Gesture Setup ==========

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
            binding.swipeText.alpha = 1f - progress
        }

        return true
    }

    private fun handleDragEnd(event: MotionEvent): Boolean {
        if (!isDragging) return false

        isDragging = false
        val deltaY = event.rawY - initialY

        if (deltaY < -DRAG_THRESHOLD) {
            animateAvatarAndNavigate()
        } else {
            animateAvatarBack()
            startArrowAnimations()
        }

        return true
    }

    // ========== Avatar Animations ==========

    private fun animateAvatarAndNavigate() {
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
                presenter.completeOnboarding()
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

        binding.swipeText.animate()
            .alpha(1f)
            .setDuration(TEXT_FADE_DURATION)
            .start()
    }

    private fun fadeOutElements() {
        binding.swipeText.animate()
            .alpha(0f)
            .setDuration(FADE_OUT_DURATION)
            .start()

        binding.arrow1.animate().alpha(0f).setDuration(FADE_OUT_DURATION).start()
        binding.arrow2.animate().alpha(0f).setDuration(FADE_OUT_DURATION).start()
        binding.arrow3.animate().alpha(0f).setDuration(FADE_OUT_DURATION).start()
    }

    // ========== Swipe Gesture Setup ==========

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSwipeGesture() {
        gestureDetector = GestureDetectorCompat(
            requireContext(),
            SwipeGestureListener()
        )

        binding.root.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }
    }

    private inner class SwipeGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            e1?.let {
                val deltaY = it.y - e2.y
                if (deltaY > SWIPE_THRESHOLD &&
                    abs(velocityY) > SWIPE_VELOCITY_THRESHOLD &&
                    !isDragging) {
                    animateSwipeUpAndNavigate(binding.swipeUpContainer)
                    return true
                }
            }
            return false
        }
    }

    private fun animateSwipeUpAndNavigate(view: View) {
        view.animate()
            .translationY(-view.height.toFloat() - 100f)
            .alpha(0f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(SWIPE_ANIMATION_DURATION)
            .withEndAction {
                presenter.completeOnboarding()
            }
            .start()
    }

    // ========== Arrow Animations ==========

    private fun startArrowAnimations() {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.arrow_animation)

        binding.arrow1.startAnimation(animation)

        binding.arrow2.postDelayed({
            if (isAdded) {
                binding.arrow2.startAnimation(
                    AnimationUtils.loadAnimation(requireContext(), R.anim.arrow_animation)
                )
            }
        }, ARROW_DELAY_SHORT)

        binding.arrow3.postDelayed({
            if (isAdded) {
                binding.arrow3.startAnimation(
                    AnimationUtils.loadAnimation(requireContext(), R.anim.arrow_animation)
                )
            }
        }, ARROW_DELAY_LONG)
    }

    private fun pauseArrowAnimations() {
        binding.arrow1.clearAnimation()
        binding.arrow2.clearAnimation()
        binding.arrow3.clearAnimation()
    }

    // ========== OnboardingView Implementation ==========

    override fun navigateToHome() {
        if (isAdded) {
            val action = OnboardingFragmentDirections.actionOnboardingToHome()
            findNavController().navigate(action)
        }
    }

    // ========== Lifecycle ==========

    override fun onDestroyView() {
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDestroyView()
    }

    // ========== Constants ==========

    companion object {
        private const val TOTAL_PAGES = 4

        // Alpha values
        private const val ALPHA_ENABLED = 1f
        private const val ALPHA_DISABLED = 0.3f

        // Arrow animation
        private const val ARROW_ANIMATION_DURATION = 100L
        private const val ARROW_ANIMATION_TOTAL_DURATION = 200L
        private const val ARROW_DELAY_SHORT = 200L
        private const val ARROW_DELAY_LONG = 400L

        // Drag gesture
        private const val DRAG_THRESHOLD = 200f
        private const val DRAG_RESISTANCE = 0.8f

        // Avatar animations
        private const val AVATAR_SCALE_UP = 1.1f
        private const val AVATAR_SCALE_DURATION = 100L
        private const val AVATAR_FLY_DURATION = 400L
        private const val AVATAR_BACK_DURATION = 400L

        // Fade animations
        private const val FADE_OUT_DURATION = 200L
        private const val TEXT_FADE_DURATION = 300L

        // Swipe gesture
        private const val SWIPE_THRESHOLD = 100f
        private const val SWIPE_VELOCITY_THRESHOLD = 100f
        private const val SWIPE_ANIMATION_DURATION = 300L

        // Property names
        private const val SCALE_X = "scaleX"
        private const val SCALE_Y = "scaleY"
    }
}