package com.qurio.trivia.ui.result

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.base.BaseFragment
import com.qurio.trivia.databinding.FragmentGameResultBinding
import com.qurio.trivia.utils.Constants
import javax.inject.Inject

class GameResultFragment : BaseFragment<FragmentGameResultBinding, GameResultPresenter>(),
    GameResultView {

    @Inject
    override lateinit var presenter: GameResultPresenter

    override val binding: FragmentGameResultBinding by lazy {
        FragmentGameResultBinding.inflate(layoutInflater)
    }

    private val args: GameResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackPressHandler()
    }

    override fun setupViews() {
        displayResults()
        setupClickListeners()
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            presenter.backToHome()
        }
    }

    private fun setupClickListeners() {
        binding.layoutActionButtons.apply {
            btnPlayAgain.setOnClickListener { presenter.playAgain() }
            btnBackToHome.setOnClickListener { presenter.backToHome() }
            btnShare.setOnClickListener { shareResults() }
        }
    }

    private fun displayResults() {
        val gameStats = GameStats.from(args, Constants.QUESTIONS_PER_GAME)

        toggleResultSections(gameStats.isWon)

        if (gameStats.isWon) {
            displayStars(gameStats.stars)
        }

        displayReward(gameStats.coins)
        displayStatistics(gameStats)
        updateShareButton(gameStats.isWon)
    }

    private fun toggleResultSections(isWon: Boolean) {
        binding.victorySection.root.visibility = if (isWon) View.VISIBLE else View.GONE
        binding.loseSection.root.visibility = if (isWon) View.GONE else View.VISIBLE
    }

    private fun displayStars(stars: Int) {
        binding.victorySection.apply {
            ivStar1.visibility = if (stars >= 1) View.VISIBLE else View.GONE
            ivStar2.visibility = if (stars >= 2) View.VISIBLE else View.GONE
            ivStar3.visibility = if (stars >= 3) View.VISIBLE else View.GONE
        }
    }

    private fun displayReward(coins: Int) {
        binding.layoutResultContent.tvCoinsEarned.text = coins.toString()
    }

    private fun displayStatistics(stats: GameStats) {
        binding.layoutResultContent.layoutStatistics.apply {
            // Correct stats
            cardCorrect.findViewById<TextView>(R.id.tv_stat_label).text = getString(R.string.correct)
            cardCorrect.findViewById<TextView>(R.id.tv_stat_value).text = stats.correct.toString()

            // Incorrect stats
            cardIncorrect.findViewById<TextView>(R.id.tv_stat_label).text = getString(R.string.incorrect)
            cardIncorrect.findViewById<TextView>(R.id.tv_stat_value).text = stats.incorrect.toString()

            // Skipped stats
            cardSkipped.findViewById<TextView>(R.id.tv_stat_label).text = getString(R.string.skipped)
            cardSkipped.findViewById<TextView>(R.id.tv_stat_value).text = stats.skipped.toString()
        }
    }

    private fun updateShareButton(isWon: Boolean) {
        binding.layoutActionButtons.btnShare.text = getString(
            if (isWon) R.string.share_win_with_friends else R.string.share_disappointment
        )
    }

    private fun shareResults() {
        val gameStats = GameStats.from(args, Constants.QUESTIONS_PER_GAME)
        val shareText = buildShareMessage(gameStats.stars)

        startActivity(Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            },
            getString(R.string.share_with_friends)
        ))
    }

    private fun buildShareMessage(stars: Int): String {
        return if (stars > 0) {
            getString(
                R.string.share_win_message,
                args.categoryName,
                args.correctAnswers,
                Constants.QUESTIONS_PER_GAME
            )
        } else {
            getString(R.string.share_lose_message, args.categoryName)
        }
    }

    override fun navigateToHome() {
        findNavController().navigate(R.id.action_result_to_home)
    }

    override fun navigateToPlayAgain() {
        findNavController().navigate(R.id.action_result_to_home)
    }

    // Data class to encapsulate game statistics
    private data class GameStats(
        val correct: Int,
        val incorrect: Int,
        val skipped: Int,
        val stars: Int,
        val coins: Int,
        val isWon: Boolean
    ) {
        companion object {
            fun from(args: GameResultFragmentArgs, totalQuestions: Int): GameStats {
                val stars = calculateStars(args.correctAnswers, args.skippedAnswers, totalQuestions)
                return GameStats(
                    correct = args.correctAnswers,
                    incorrect = args.incorrectAnswers,
                    skipped = args.skippedAnswers,
                    stars = stars,
                    coins = calculateCoins(stars),
                    isWon = stars > 0
                )
            }

            private fun calculateStars(correct: Int, skipped: Int, total: Int): Int {
                val correctPercentage = (correct.toFloat() / total) * 100
                return when {
                    correct == total && skipped == 0 -> 3
                    correctPercentage >= 80 && skipped <= 2 -> 2
                    correctPercentage >= 50 -> 1
                    else -> 0
                }
            }

            private fun calculateCoins(stars: Int): Int {
                return when (stars) {
                    3 -> Constants.Rewards.THREE_STAR_COINS
                    2 -> Constants.Rewards.TWO_STAR_COINS
                    1 -> Constants.Rewards.ONE_STAR_COINS
                    else -> Constants.Rewards.LOSE_COINS
                }
            }
        }
    }
}