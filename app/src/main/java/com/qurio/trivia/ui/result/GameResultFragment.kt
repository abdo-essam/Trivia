package com.qurio.trivia.ui.result

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.base.BaseFragment
import com.qurio.trivia.databinding.FragmentGameResultBinding
import com.qurio.trivia.utils.Constants
import javax.inject.Inject

class GameResultFragment : BaseFragment<FragmentGameResultBinding, GameResultPresenter>(), GameResultView {

    @Inject
    override lateinit var presenter: GameResultPresenter

    override val binding: FragmentGameResultBinding by lazy {
        FragmentGameResultBinding.inflate(layoutInflater)
    }

    private val args: GameResultFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        return binding.root
    }

    override fun setupViews() {
        calculateAndDisplayResults()

        binding.btnPlayAgain.setOnClickListener {
            presenter.playAgain()
        }

        binding.btnBackToHome.setOnClickListener {
            presenter.backToHome()
        }

        binding.btnShareWithFriends.setOnClickListener {
            shareResults()
        }
    }

    override fun setupObservers() {
        // No observers needed
    }

    private fun calculateAndDisplayResults() {
        val totalQuestions = Constants.QUESTIONS_PER_GAME
        val correctAnswers = args.correctAnswers
        val incorrectAnswers = args.incorrectAnswers
        val skippedAnswers = args.skippedAnswers

        // Calculate stars
        val correctPercentage = (correctAnswers.toFloat() / totalQuestions) * 100
        val stars = when {
            correctAnswers == totalQuestions && skippedAnswers == 0 -> 3
            correctPercentage >= 80 && skippedAnswers <= 2 -> 2
            correctPercentage >= 50 -> 1
            else -> 0
        }

        // Calculate coins
        val coinsEarned = when (stars) {
            3 -> Constants.Rewards.THREE_STAR_COINS
            2 -> Constants.Rewards.TWO_STAR_COINS
            1 -> Constants.Rewards.ONE_STAR_COINS
            else -> Constants.Rewards.LOSE_COINS
        }

        // Display results
        displayResults(stars, coinsEarned, correctAnswers, incorrectAnswers, skippedAnswers)
    }

    private fun displayResults(stars: Int, coins: Int, correct: Int, incorrect: Int, skipped: Int) {
        // Display stars
        binding.ivStar1.visibility = if (stars >= 1) View.VISIBLE else View.INVISIBLE
        binding.ivStar2.visibility = if (stars >= 2) View.VISIBLE else View.INVISIBLE
        binding.ivStar3.visibility = if (stars >= 3) View.VISIBLE else View.INVISIBLE

        // Display title and ribbon color based on performance
        when (stars) {
            3 -> {
                binding.tvResultTitle.text = "Great Job"
                binding.ivResultRibbon.setImageResource(R.drawable.ribbon_blue)
            }
            2 -> {
                binding.tvResultTitle.text = "Great Job"
                binding.ivResultRibbon.setImageResource(R.drawable.ribbon_blue)
            }
            1 -> {
                binding.tvResultTitle.text = "Great Job"
                binding.ivResultRibbon.setImageResource(R.drawable.ribbon_blue)
            }
            else -> {
                binding.tvResultTitle.text = "You Lose"
                binding.ivResultRibbon.setImageResource(R.drawable.ribbon_blue)
            }
        }

        // Display reward
        binding.tvReward.text = "reward"
        binding.tvCoinsEarned.text = coins.toString()

        // Display statistics
        binding.tvCorrectCount.text = correct.toString()
        binding.tvIncorrectCount.text = incorrect.toString()
        binding.tvSkippedCount.text = skipped.toString()
    }

    private fun shareResults() {
        val shareText = "I just played ${args.categoryName} trivia and got ${args.correctAnswers}/${Constants.QUESTIONS_PER_GAME} correct! 🎉"

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        startActivity(Intent.createChooser(shareIntent, "Share your results"))
    }

    override fun navigateToHome() {
        findNavController().navigate(R.id.action_result_to_home)
    }

    override fun navigateToPlayAgain() {
        findNavController().navigateUp()
        findNavController().navigateUp() // Go back to difficulty selection
    }
}