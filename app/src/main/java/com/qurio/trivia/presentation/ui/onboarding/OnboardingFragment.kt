package com.qurio.trivia.presentation.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.databinding.FragmentOnboardingBinding
import com.qurio.trivia.databinding.LayoutSwipeUpBinding
import com.qurio.trivia.presentation.base.BaseFragment
import com.qurio.trivia.presentation.ui.onboarding.adapter.OnboardingAdapter
import com.qurio.trivia.presentation.ui.onboarding.data.OnboardingDataProvider
import com.qurio.trivia.presentation.ui.onboarding.handlers.OnboardingNavigationHandler
import com.qurio.trivia.presentation.ui.onboarding.handlers.SwipeUpGestureHandler
import javax.inject.Inject

class OnboardingFragment : BaseFragment<FragmentOnboardingBinding, OnboardingView, OnboardingPresenter>(),
    OnboardingView {

    @Inject
    lateinit var onboardingPresenter: OnboardingPresenter

    @Inject
    lateinit var dataProvider: OnboardingDataProvider

    private lateinit var onboardingAdapter: OnboardingAdapter
    private lateinit var navigationHandler: OnboardingNavigationHandler
    private lateinit var swipeHandler: SwipeUpGestureHandler
    private lateinit var swipeUpBinding: LayoutSwipeUpBinding

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
        setupSwipeUpBinding()
        setupViewPager()
        setupNavigationHandler()
        setupSwipeGestureHandler()
    }

    // ========== Setup Methods ==========

    private fun setupSwipeUpBinding() {
        swipeUpBinding = LayoutSwipeUpBinding.bind(binding.swipeUpLayout.root)
    }

    private fun setupViewPager() {
        val onboardingItems = dataProvider.getOnboardingItems(requireContext())
        onboardingAdapter = OnboardingAdapter(onboardingItems)

        binding.onboardingViewPager.apply {
            adapter = onboardingAdapter
            offscreenPageLimit = 1
        }
    }

    private fun setupNavigationHandler() {
        navigationHandler = OnboardingNavigationHandler(
            binding = binding,
            viewPager = binding.onboardingViewPager,
            totalPages = OnboardingDataProvider.TOTAL_PAGES
        )
    }

    private fun setupSwipeGestureHandler() {
        swipeHandler = SwipeUpGestureHandler(
            fragment = this,
            binding = swipeUpBinding,
            onSwipeComplete = {
                presenter.completeOnboarding()
            }
        )
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
        navigationHandler.cleanup()
        swipeHandler.cleanup()
        super.onDestroyView()
    }
}