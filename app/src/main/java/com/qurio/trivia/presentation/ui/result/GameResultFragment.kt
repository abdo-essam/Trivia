package com.qurio.trivia.presentation.ui.result

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.databinding.FragmentGameResultBinding
import com.qurio.trivia.presentation.base.BaseFragment
import com.qurio.trivia.utils.Constants
import javax.inject.Inject

/**
 * Fragment displaying game results with statistics and actions
 */
class GameResultFragment : BaseFragment<FragmentGameResultBinding, GameResultView, GameResultPresenter>(),
    GameResultView {

    @Inject
    lateinit var gameResultPresenter: GameResultPresenter

    private val args: GameResultFragmentArgs by navArgs()

    companion object {
        private const val TAG = "GameResultFragment"
    }

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
        val gameStats = calculateGameStats()
        displayResults(gameStats)
        setupClickListeners()
        saveGameResult(gameStats)
    }

    // ========== Setup ==========

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

    // ========== Calculate Stats ==========

    private fun calculateGameStats(): GameStats {
        val stars = presenter.calculateStars(
            correct = args.correctAnswers,
            skipped = args.skippedAnswers,
            total = Constants.QUESTIONS_PER_GAME
        )

        val coins = presenter.calculateCoins(stars)

        val percentage = if (Constants.QUESTIONS_PER_GAME > 0) {
            ((args.correctAnswers.toFloat() / Constants.QUESTIONS_PER_GAME) * 100).toInt()
        } else 0

        return GameStats(
            correct = args.correctAnswers,
            incorrect = args.incorrectAnswers,
            skipped = args.skippedAnswers,
            stars = stars,
            coins = coins,
            isWon = stars > 0,
            percentage = percentage
        )
    }

    // ========== Display Results ==========

    private fun displayResults(stats: GameStats) {
        Log.d(TAG, "Displaying results: $stats")

        toggleResultSections(stats.isWon)

        if (stats.isWon) {
            displayVictorySection(stats.stars)
        } else {
            displayLoseSection()
        }

        displayReward(stats.coins)
        displayStatistics(stats)
        updateShareButton(stats.isWon)
    }

    private fun toggleResultSections(isWon: Boolean) {
        binding.victorySection.root.isVisible = isWon
        binding.loseSection.root.isVisible = !isWon
    }

    private fun displayVictorySection(stars: Int) {
        binding.victorySection.apply {
            ivStar1.isVisible = stars >= 1
            ivStar2.isVisible = stars >= 2
            ivStar3.isVisible = stars >= 3
        }
    }

    private fun displayLoseSection() {
        // Lose section is already set up in XML
        // Could add animations here if needed
    }

    private fun displayReward(coins: Int) {
        val sign = if (coins >= 0) "+" else ""
        val text = "$sign$coins"

        binding.layoutResultContent.tvCoinsEarned.apply {
            this.text = text
            setTextColor(ContextCompat.getColor(
                requireContext(),
                if (coins >= 0) R.color.green else R.color.red
            ))
        }
    }

    private fun displayStatistics(stats: GameStats) {
        binding.layoutResultContent.layoutStatistics.apply {
            setupStatCard(
                card = cardCorrect,
                label = getString(R.string.correct),
                value = stats.correct.toString(),
                colorRes = R.color.green
            )

            setupStatCard(
                card = cardIncorrect,
                label = getString(R.string.incorrect),
                value = stats.incorrect.toString(),
                colorRes = R.color.red
            )

            setupStatCard(
                card = cardSkipped,
                label = getString(R.string.skipped),
                value = stats.skipped.toString(),
                colorRes = R.color.orange
            )
        }
    }

    private fun setupStatCard(card: View, label: String, value: String, colorRes: Int) {
        card.findViewById<TextView>(R.id.tv_stat_label)?.text = label
        card.findViewById<TextView>(R.id.tv_stat_value)?.apply {
            text = value
            setTextColor(ContextCompat.getColor(requireContext(), colorRes))
        }
    }

    private fun updateShareButton(isWon: Boolean) {
        binding.layoutActionButtons.btnShare.text = getString(
            if (isWon) R.string.share_win_with_friends else R.string.share_disappointment
        )
    }

    // ========== Save Game Result ==========

    private fun saveGameResult(stats: GameStats) {
        presenter.saveGameResult(
            category = args.categoryName,
            correctAnswers = args.correctAnswers,
            incorrectAnswers = args.incorrectAnswers,
            skippedAnswers = args.skippedAnswers,
            timeTaken = args.totalTime
        )
    }

    // ========== GameResultView Implementation ==========

    override fun navigateToHome() {
        if (isAdded && view != null) {
            findNavController().navigate(R.id.action_result_to_home)
        }
    }

    override fun navigateToPlayAgain() {
        if (isAdded && view != null) {
            findNavController().navigate(R.id.action_result_to_home)
        }
    }

    override fun onGameResultSaved(coins: Int, stars: Int) {
        Log.d(TAG, "✓ Game result saved: coins=$coins, stars=$stars")
        // Could show success animation here
    }

    // ========== Share Functionality ==========

    private fun shareResults() {
        val stats = calculateGameStats()
        val shareText = buildShareMessage(stats)

        try {
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
        } catch (e: Exception) {
            Log.e(TAG, "Error sharing results", e)
            showError(getString(R.string.share_failed))
        }
    }

    private fun buildShareMessage(stats: GameStats): String {
        return if (stats.isWon) {
            getString(
                R.string.share_win_message,
                args.categoryName,
                stats.correct,
                Constants.QUESTIONS_PER_GAME
            )
        } else {
            getString(
                R.string.share_lose_message,
                args.categoryName,
            )
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
    )
}