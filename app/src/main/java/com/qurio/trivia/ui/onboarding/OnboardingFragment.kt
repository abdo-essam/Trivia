package com.qurio.trivia.ui.onboarding

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.base.BaseFragment
import com.qurio.trivia.databinding.FragmentOnboardingBinding
import com.qurio.trivia.ui.adapters.OnboardingAdapter
import javax.inject.Inject

class OnboardingFragment : BaseFragment<FragmentOnboardingBinding, OnboardingPresenter>(), OnboardingView {

    @Inject
    override lateinit var presenter: OnboardingPresenter

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override val binding: FragmentOnboardingBinding by lazy {
        FragmentOnboardingBinding.inflate(layoutInflater)
    }

    private lateinit var onboardingAdapter: OnboardingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        return binding.root
    }

    override fun setupViews() {
        setupViewPager()

        binding.btnGetStarted.setOnClickListener {
            presenter.completeOnboarding()
        }

        binding.btnSkip.setOnClickListener {
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
                "Welcome to Qurio!",
                "Embark on an exciting trivia adventure with your favorite characters!"
            ),
            OnboardingItem(
                R.drawable.onboarding_2,
                "Choose Your Character",
                "Pick from amazing characters, each with their own unique personality and story!"
            ),
            OnboardingItem(
                R.drawable.onboarding_3,
                "Test Your Knowledge",
                "Challenge yourself across multiple categories and difficulty levels!"
            ),
            OnboardingItem(
                R.drawable.onboarding_4,
                "Earn Rewards",
                "Collect coins, unlock achievements, and climb the leaderboards!"
            )
        )

        onboardingAdapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.adapter = onboardingAdapter

        // Setup dots indicator
        binding.dotsIndicator.setViewPager2(binding.viewPager)

        // Show/hide buttons based on current page
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == onboardingItems.size - 1) {
                    binding.btnGetStarted.visibility = View.VISIBLE
                    binding.btnSkip.visibility = View.GONE
                } else {
                    binding.btnGetStarted.visibility = View.GONE
                    binding.btnSkip.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun navigateToHome() {
        val action = OnboardingFragmentDirections.actionOnboardingToHome()
        findNavController().navigate(action)
    }
}

data class OnboardingItem(
    val imageRes: Int,
    val title: String,
    val description: String
)