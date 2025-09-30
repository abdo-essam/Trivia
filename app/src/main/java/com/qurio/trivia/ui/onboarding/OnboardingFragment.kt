package com.qurio.trivia.ui.onboarding

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        return binding.root
    }

    override fun setupViews() {
        setupViewPager()
        setupArrows()
        setupSwipeGesture()
        startArrowAnimations()

        binding.swipeUpContainer.setOnClickListener {
            presenter.completeOnboarding()
        }
    }

    override fun setupObservers() {
        // No observers needed
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
                R.drawable.ic_crown,
                "Challenge and win",
                "Answer quickly, earn points, and share with your friends! Each trivia category is a new experience."
            ),
            OnboardingItem(
                R.drawable.ic_trophy,
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



    private fun setupArrows() {
        binding.arrowLeft.setOnClickListener {
            if (currentPage > 0) {
                animateArrowPress(binding.arrowLeft) {
                    binding.viewPager.currentItem = currentPage - 1
                }
            }
        }

        binding.arrowRight.setOnClickListener {
            if (currentPage < totalPages - 1) {
                animateArrowPress(binding.arrowRight) {
                    binding.viewPager.currentItem = currentPage + 1
                }
            }
        }
    }

    private fun updateArrowVisibility() {
        binding.arrowLeft.alpha = if (currentPage > 0) 1f else 0.3f
        binding.arrowRight.alpha = if (currentPage < totalPages - 1) 1f else 0.3f
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
                        presenter.completeOnboarding()
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