package com.qurio.trivia.ui.onboarding

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
import com.qurio.trivia.base.BaseFragment
import com.qurio.trivia.databinding.FragmentOnboardingBinding
import com.qurio.trivia.ui.adapters.OnboardingAdapter
import javax.inject.Inject
import kotlin.math.abs

class OnboardingFragment : BaseFragment<FragmentOnboardingBinding, OnboardingPresenter>(), OnboardingView {

    @Inject
    override lateinit var presenter: OnboardingPresenter

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override val binding: FragmentOnboardingBinding by lazy {
        FragmentOnboardingBinding.inflate(layoutInflater)
    }

    private lateinit var onboardingAdapter: OnboardingAdapter
    private lateinit var gestureDetector: GestureDetectorCompat
    private var currentPage = 0
    private val totalPages = 4

    // Drag gesture variables
    private var initialY = 0f
    private var isDragging = false
    private var dragThreshold = 200f // pixels to trigger navigation
    private var initialAvatarY = 0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        return binding.root
    }

    override fun setupViews() {
        setupViewPager()
        setupArrows()
        setupSwipeGesture()
        setupDragGesture()
        startArrowAnimations()
    }

    private fun setupViewPager() {
        val onboardingItems = listOf(
            OnboardingItem(
                R.drawable.onboarding_1,
                "Welcome to Qurio",
                "Welcome to the world of Qurio, where questions spark curiosity and prizes await the smartest. Ready to begin the challenge?"
            ),
            OnboardingItem(
                R.drawable.onboarding_2,
                "Choose your character",
                "Each hero has their own unique style! Choose from unique characters and start your adventure in your own style."
            ),
            OnboardingItem(
                R.drawable.onboarding_3,
                "Challenge and win",
                "Answer quickly, earn points, and share with your friends! Each trivia category is a new experience."
            ),
            OnboardingItem(
                R.drawable.onboarding_4,
                "Collect them all!",
                "Unlock characters, earn badges, and climb the leaderboards. Qurio is merciless, but you can handle it."
            )
        )

        onboardingAdapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.adapter = onboardingAdapter
        binding.viewPager.offscreenPageLimit = 1

        // ViewPager page change callback
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
                updateArrowVisibility()
            }
        })

        updateArrowVisibility()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDragGesture() {
        // Set touch listener on the entire container but move only the avatar
        binding.swipeUpContainer.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialY = event.rawY
                    initialAvatarY = binding.avatarContainer.translationY
                    isDragging = true

                    // Add haptic feedback
                    binding.avatarContainer.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

                    // Scale up avatar slightly when touched
                    binding.avatarContainer.animate()
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .setDuration(100)
                        .start()

                    // Pause arrow animations
                    binding.arrow1.clearAnimation()
                    binding.arrow2.clearAnimation()
                    binding.arrow3.clearAnimation()

                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isDragging) {
                        val deltaY = event.rawY - initialY

                        // Only allow upward movement
                        if (deltaY < 0) {
                            // Apply some resistance
                            val translation = deltaY * 0.8f
                            binding.avatarContainer.translationY = initialAvatarY + translation

                            // Rotate avatar slightly as it moves
                            val rotation = (translation / dragThreshold) * -15f
                            binding.avatarContainer.rotation = rotation

                            // Update alpha based on drag distance
                            val progress = abs(translation) / dragThreshold
                            binding.avatarContainer.alpha = 1f - (progress * 0.2f)

                            // Update text alpha to fade as avatar moves up
                            binding.swipeText.alpha = 1f - progress
                        }
                    }
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (isDragging) {
                        isDragging = false
                        val deltaY = event.rawY - initialY

                        if (deltaY < -dragThreshold) {
                            // Trigger navigation with animation
                            animateAvatarAndNavigate()
                        } else {
                            // Snap avatar back to original position
                            animateAvatarBack()
                            // Resume arrow animations
                            startArrowAnimations()
                        }
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun animateAvatarAndNavigate() {
        // Animate avatar flying up
        binding.avatarContainer.animate()
            .translationY(-binding.root.height.toFloat())
            .alpha(0f)
            .scaleX(0.5f)
            .scaleY(0.5f)
            .rotation(-30f)
            .setDuration(400)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                presenter.completeOnboarding()
            }
            .start()

        // Fade out other elements
        binding.swipeText.animate()
            .alpha(0f)
            .setDuration(200)
            .start()

        binding.arrow1.animate().alpha(0f).setDuration(200).start()
        binding.arrow2.animate().alpha(0f).setDuration(200).start()
        binding.arrow3.animate().alpha(0f).setDuration(200).start()
    }

    private fun animateAvatarBack() {
        // Animate avatar back to position with bounce
        binding.avatarContainer.animate()
            .translationY(0f)
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .rotation(0f)
            .setDuration(400)
            .setInterpolator(OvershootInterpolator(2f))
            .start()

        // Restore text alpha
        binding.swipeText.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }
    private fun setupArrows() {
        binding.arrowLeftContainer.setOnClickListener {
            if (currentPage > 0) {
                animateArrowPress(binding.arrowLeftContainer) {
                    binding.viewPager.currentItem = currentPage - 1
                }
            }
        }

        binding.arrowRightContainer.setOnClickListener {
            if (currentPage < totalPages - 1) {
                animateArrowPress(binding.arrowRightContainer) {
                    binding.viewPager.currentItem = currentPage + 1
                }
            }
        }
    }

    private fun updateArrowVisibility() {
        binding.arrowLeftContainer.alpha = if (currentPage > 0) 1f else 0.3f
        binding.arrowRightContainer.alpha = if (currentPage < totalPages - 1) 1f else 0.3f
    }

    private fun animateArrowPress(arrow: View, onComplete: () -> Unit) {
        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            arrow,
            PropertyValuesHolder.ofFloat("scaleX", 1f, 0.8f),
            PropertyValuesHolder.ofFloat("scaleY", 1f, 0.8f)
        ).apply {
            duration = 100
        }

        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
            arrow,
            PropertyValuesHolder.ofFloat("scaleX", 0.8f, 1f),
            PropertyValuesHolder.ofFloat("scaleY", 0.8f, 1f)
        ).apply {
            duration = 100
            startDelay = 100
        }

        scaleDown.start()
        scaleUp.start()

        arrow.postDelayed(onComplete, 200)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSwipeGesture() {
        gestureDetector = GestureDetectorCompat(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                e1?.let {
                    if (e1.y - e2.y > 100 && abs(velocityY) > 100) {
                        // Don't trigger if we're already dragging
                        if (!isDragging) {
                            animateSwipeUpAndNavigate(binding.swipeUpContainer)
                        }
                        return true
                    }
                }
                return false
            }
        })

        binding.root.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }
    }

    private fun animateSwipeUpAndNavigate(view: View) {
        view.animate()
            .translationY(-view.height.toFloat() - 100f)
            .alpha(0f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(300)
            .withEndAction {
                presenter.completeOnboarding()
            }
            .start()
    }

    private fun startArrowAnimations() {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.arrow_animation)

        binding.arrow1.startAnimation(animation)
        binding.arrow2.postDelayed({
            binding.arrow2.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.arrow_animation))
        }, 200)
        binding.arrow3.postDelayed({
            binding.arrow3.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.arrow_animation))
        }, 400)
    }

    override fun navigateToHome() {
        val action = OnboardingFragmentDirections.actionOnboardingToHome()
        findNavController().navigate(action)
    }
}