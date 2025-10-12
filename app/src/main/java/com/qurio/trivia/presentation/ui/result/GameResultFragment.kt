package com.qurio.trivia.presentation.ui.result

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.presentation.base.BaseFragment
import com.qurio.trivia.databinding.FragmentGameResultBinding
import com.qurio.trivia.utils.Constants
import javax.inject.Inject

class GameResultFragment : BaseFragment<FragmentGameResultBinding, GameResultView, GameResultPresenter>(),
    GameResultView {

    @Inject
    lateinit var gameResultPresenter: GameResultPresenter

    private val args: GameResultFragmentArgs by navArgs()

    // ========== BaseFragment Implementation ==========

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGameResultBinding {
        return FragmentGameResultBinding.inflate(inflater, container, false)
    }

    override fun initPresenter(): GameResultPresenter = gameResultPresenter

    override fun setupViews() {
        displayResults()
        setupClickListeners()
        saveGameResult()
    }

    // ========== Setup Methods ==========

    private fun setupClickListeners() {
        binding.layoutActionButtons.apply {
            btnPlayAgain.setOnClickListener {
                Log.d(TAG, "Play Again clicked")
                presenter.playAgain()
            }
            btnBackToHome.setOnClickListener {
                Log.d(TAG, "Back to Home clicked")
                presenter.backToHome()
            }
            btnShare.setOnClickListener {
                Log.d(TAG, "Share clicked")
                shareResults()
            }
        }
    }

    // ========== Display Results ==========

    private fun displayResults() {
        val gameStats = GameStats.from(args, Constants.QUESTIONS_PER_GAME)

        Log.d(TAG, "Displaying results: $gameStats")

        toggleResultSections(gameStats.isWon)

        if (gameStats.isWon) {
            displayStars(gameStats.stars)
        }

        displayReward(gameStats.coins)
        displayStatistics(gameStats)
        updateShareButton(gameStats.isWon)
    }

    private fun toggleResultSections(isWon: Boolean) {
        binding.victorySection.root.isVisible = isWon
        binding.loseSection.root.isVisible = !isWon
    }

    private fun displayStars(stars: Int) {
        binding.victorySection.apply {
            ivStar1.isVisible = stars >= 1
            ivStar2.isVisible = stars >= 2
            ivStar3.isVisible = stars >= 3
        }
    }

    private fun displayReward(coins: Int) {
        val sign = if (coins >= 0) "+" else ""
        binding.layoutResultContent.tvCoinsEarned.text = "$sign$coins"
    }

    private fun displayStatistics(stats: GameStats) {
        binding.layoutResultContent.layoutStatistics.apply {
            // Correct stats
            setupStatCard(
                cardCorrect,
                getString(R.string.correct),
                stats.correct.toString(),
                R.color.green
            )

            // Incorrect stats
            setupStatCard(
                cardIncorrect,
                getString(R.string.incorrect),
                stats.incorrect.toString(),
                R.color.red
            )

            // Skipped stats
            setupStatCard(
                cardSkipped,
                getString(R.string.skipped),
                stats.skipped.toString(),
                R.color.orange
            )
        }
    }

    private fun setupStatCard(card: View, label: String, value: String, colorRes: Int) {
        card.findViewById<TextView>(R.id.tv_stat_label).text = label
        card.findViewById<TextView>(R.id.tv_stat_value).apply {
            text = value
            setTextColor(requireContext().getColor(colorRes))
        }
    }


    private fun updateShareButton(isWon: Boolean) {
        binding.layoutActionButtons.btnShare.text = getString(
            if (isWon) R.string.share_win_with_friends else R.string.share_disappointment
        )
    }

    // ========== Save Game Result ==========

    private fun saveGameResult() {
        val gameStats = GameStats.from(args, Constants.QUESTIONS_PER_GAME)

        presenter.saveGameResult(
            category = args.categoryName,
            totalQuestions = Constants.QUESTIONS_PER_GAME,
            correctAnswers = args.correctAnswers,
            incorrectAnswers = args.incorrectAnswers,
            skippedAnswers = args.skippedAnswers,
            stars = gameStats.stars,
            coins = gameStats.coins,
            timeTaken = args.totalTime
        )
    }

    // ========== GameResultView Implementation ==========

    override fun navigateToHome() {
        if (isAdded) {
            findNavController().navigate(R.id.action_result_to_home)
        }
    }

    override fun navigateToPlayAgain() {
        if (isAdded) {
            // Navigate back to home, user can select category again
            findNavController().navigate(R.id.action_result_to_home)
        }
    }

    override fun onGameResultSaved(coins: Int, stars: Int) {
        Log.d(TAG, "Game result saved successfully: coins=$coins, stars=$stars")
        // Optional: Show a success animation or toast
    }

    // ========== Share Functionality ==========

    private fun shareResults() {
        val gameStats = GameStats.from(args, Constants.QUESTIONS_PER_GAME)
        val shareText = buildShareMessage(gameStats)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        }

        startActivity(Intent.createChooser(
            shareIntent,
            getString(R.string.share_with_friends)
        ))
    }

    private fun buildShareMessage(stats: GameStats): String {
        return if (stats.isWon) {
            getString(
                R.string.share_win_message,
                args.categoryName,
                stats.correct,
                Constants.QUESTIONS_PER_GAME,
            )
        } else {
            getString(R.string.share_lose_message, args.categoryName)
        }
    }

    // ========== Data Classes ==========

    private data class GameStats(
        val correct: Int,
        val incorrect: Int,
        val skipped: Int,
        val stars: Int,
        val coins: Int,
        val isWon: Boolean,
        val percentage: Int
    ) {
        companion object {
            fun from(args: GameResultFragmentArgs, totalQuestions: Int): GameStats {
                val stars = calculateStars(
                    correct = args.correctAnswers,
                    skipped = args.skippedAnswers,
                    total = totalQuestions
                )
                val percentage = calculatePercentage(args.correctAnswers, totalQuestions)

                return GameStats(
                    correct = args.correctAnswers,
                    incorrect = args.incorrectAnswers,
                    skipped = args.skippedAnswers,
                    stars = stars,
                    coins = calculateCoins(stars),
                    isWon = stars > 0,
                    percentage = percentage
                )
            }

            private fun calculateStars(correct: Int, skipped: Int, total: Int): Int {
                val correctPercentage = (correct.toFloat() / total) * 100
                return when {
                    correct == total && skipped == 0 -> Constants.Stars.THREE_STARS
                    correctPercentage >= PERCENTAGE_TWO_STARS && skipped <= MAX_SKIPS_TWO_STARS ->
                        Constants.Stars.TWO_STARS
                    correctPercentage >= PERCENTAGE_ONE_STAR -> Constants.Stars.ONE_STAR
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

            private fun calculatePercentage(correct: Int, total: Int): Int {
                return ((correct.toFloat() / total) * 100).toInt()
            }

            private const val PERCENTAGE_TWO_STARS = 80f
            private const val PERCENTAGE_ONE_STAR = 50f
            private const val MAX_SKIPS_TWO_STARS = 2
        }
    }

    companion object {
        private const val TAG = "GameResultFragment"
    }
}