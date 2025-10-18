package com.qurio.trivia.presentation.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.qurio.trivia.QurioApp
import com.qurio.trivia.R
import com.qurio.trivia.databinding.FragmentGameResultBinding
import com.qurio.trivia.presentation.base.BaseFragment
import com.qurio.trivia.presentation.ui.result.managers.GameResultCalculator
import com.qurio.trivia.presentation.ui.result.managers.GameResultShareManager
import com.qurio.trivia.presentation.ui.result.managers.GameResultUIManager
import javax.inject.Inject

class GameResultFragment : BaseFragment<FragmentGameResultBinding, GameResultView, GameResultPresenter>(),
    GameResultView {

    @Inject
    lateinit var gameResultPresenter: GameResultPresenter

    private val args: GameResultFragmentArgs by navArgs()

    private lateinit var uiManager: GameResultUIManager
    private lateinit var calculator: GameResultCalculator
    private lateinit var shareManager: GameResultShareManager

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as QurioApp).appComponent.inject(this)
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
        initializeManagers()

        val gameStats = calculator.calculateGameStats(
            correctAnswers = args.correctAnswers,
            incorrectAnswers = args.incorrectAnswers,
            skippedAnswers = args.skippedAnswers
        )

        uiManager.displayResults(gameStats)
        setupClickListeners(gameStats)
        saveGameResult(gameStats)
    }

    private fun initializeManagers() {
        uiManager = GameResultUIManager(binding)
        calculator = GameResultCalculator()
        shareManager = GameResultShareManager(requireContext())
    }

    private fun setupClickListeners(gameStats: com.qurio.trivia.presentation.ui.result.model.GameResultStats) {
        binding.apply {
            btnPlayAgain.setOnClickListener {
                presenter.playAgain()
            }

            btnBackToHome.setOnClickListener {
                presenter.backToHome()
            }

            btnShare.setOnClickListener {
                shareManager.shareResults(args.categoryName, gameStats)
            }
        }
    }

    private fun saveGameResult(stats: com.qurio.trivia.presentation.ui.result.model.GameResultStats) {
        presenter.saveGameResult(
            category = args.categoryName,
            correctAnswers = stats.correct,
            incorrectAnswers = stats.incorrect,
            skippedAnswers = stats.skipped,
            stars = stats.stars,
            coins = stats.coins,
            timeTaken = args.totalTime
        )
    }

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
}