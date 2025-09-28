package com.qurio.trivia.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.base.BaseFragment
import com.qurio.trivia.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashFragment : BaseFragment<FragmentSplashBinding, SplashPresenter>() {

    @Inject
    override lateinit var presenter: SplashPresenter

    override val binding: FragmentSplashBinding by lazy {
        FragmentSplashBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show splash for 2 seconds then navigate
        lifecycleScope.launch {
            delay(2000)
            presenter.checkFirstLaunch()
        }
    }

    override fun setupViews() {
        // Splash screen doesn't need setup
    }

    override fun setupObservers() {
        // No observers needed
    }

    fun navigateToOnboarding() {
        val action = SplashFragmentDirections.actionSplashToOnboarding()
        findNavController().navigate(action)
    }

    fun navigateToHome() {
        val action = SplashFragmentDirections.actionSplashToHome()
        findNavController().navigate(action)
    }
}