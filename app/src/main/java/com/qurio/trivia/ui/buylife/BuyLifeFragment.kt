package com.qurio.trivia.ui.buylife

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.base.BaseFragment
import com.qurio.trivia.data.model.UserProgress
import com.qurio.trivia.databinding.FragmentBuyLifeBinding
import javax.inject.Inject

class BuyLifeFragment : BaseFragment<FragmentBuyLifeBinding, BuyLifePresenter>(), BuyLifeView {

    @Inject
    override lateinit var presenter: BuyLifePresenter

    override val binding: FragmentBuyLifeBinding by lazy {
        FragmentBuyLifeBinding.inflate(layoutInflater)
    }

    private val lifeCost = 100 // 100 coins per life

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        return binding.root
    }

    override fun setupViews() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnBuyLife.setOnClickListener {
            presenter.buyLife(lifeCost)
        }

        binding.btnWatchAd.setOnClickListener {
            presenter.watchAdForLife()
        }

        presenter.loadUserProgress()
    }

    override fun setupObservers() {
        // No observers needed
    }

    override fun displayUserProgress(userProgress: UserProgress) {
        binding.tvCurrentLives.text = userProgress.lives.toString()
        binding.tvCurrentCoins.text = userProgress.totalCoins.toString()

        // Enable/disable buy button based on coins
        binding.btnBuyLife.isEnabled = userProgress.totalCoins >= lifeCost

        if (userProgress.totalCoins < lifeCost) {
            binding.btnBuyLife.text = "Not enough coins"
            binding.btnBuyLife.backgroundTintList = android.content.res.ColorStateList.valueOf(
                requireContext().getColor(R.color.gray_dark)
            )
        } else {
            binding.btnBuyLife.text = "Buy Life - $lifeCost coins"
            binding.btnBuyLife.backgroundTintList = android.content.res.ColorStateList.valueOf(
                requireContext().getColor(R.color.blue_primary)
            )
        }
    }

    override fun showPurchaseSuccess() {
        showError("Life purchased successfully!")
        presenter.loadUserProgress() // Refresh UI
    }

    override fun showInsufficientCoins() {
        showError("Not enough coins to buy a life!")
    }

    override fun showAdReward() {
        showError("Life earned from watching ad!")
        presenter.loadUserProgress() // Refresh UI
    }

    override fun navigateBack() {
        findNavController().navigateUp()
    }
}