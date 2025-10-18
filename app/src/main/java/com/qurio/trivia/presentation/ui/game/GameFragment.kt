package com.qurio.trivia.presentation.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.qurio.trivia.QurioApp
import com.qurio.trivia.R
import com.qurio.trivia.data.model.TriviaQuestion
import com.qurio.trivia.databinding.FragmentGameBinding
import com.qurio.trivia.domain.model.Difficulty
import com.qurio.trivia.presentation.base.BaseFragment
import com.qurio.trivia.presentation.ui.dialogs.buylife.BuyLifeDialog
import com.qurio.trivia.presentation.ui.game.adapter.AnswerItemDecoration
import com.qurio.trivia.presentation.ui.game.adapter.AnswerOptionAdapter
import com.qurio.trivia.presentation.ui.game.managers.GameAnimationManager
import com.qurio.trivia.presentation.ui.game.managers.GameStateManager
import com.qurio.trivia.presentation.ui.game.managers.GameTimerManager
import com.qurio.trivia.presentation.ui.game.managers.GameUIManager
import javax.inject.Inject

class GameFragment : BaseFragment<FragmentGameBinding, GameView, GamePresenter>(), GameView {

    @Inject
    lateinit var gamePresenter: GamePresenter

    private val args: GameFragmentArgs by navArgs()

    private lateinit var uiManager: GameUIManager
    private lateinit var timerManager: GameTimerManager
    private lateinit var animationManager: GameAnimationManager
    private lateinit var stateManager: GameStateManager

    private var buyLifeDialog: BuyLifeDialog? = null

    private val answerAdapter by lazy {
        AnswerOptionAdapter { position -> handleAnswerClick(position) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as QurioApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGameBinding {
        return FragmentGameBinding.inflate(inflater, container, false)
    }

    override fun initPresenter(): GamePresenter = gamePresenter

    override fun setupViews() {
        initializeManagers()
        setupRecyclerView()
        setupClickListeners()
        loadQuestions()
    }

    private fun initializeManagers() {
        stateManager = GameStateManager()
        uiManager = GameUIManager(binding, answerAdapter)
        timerManager = GameTimerManager(binding) { presenter.timeUp() }
        animationManager = GameAnimationManager(
            binding.floatingTagsContainer,
            resources.getDimensionPixelSize(R.dimen.floating_tag_size)
        )
    }

    private fun setupRecyclerView() {
        binding.rvAnswerOptions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = answerAdapter
            addItemDecoration(
                AnswerItemDecoration(
                    resources.getDimensionPixelSize(R.dimen.answer_option_spacing)
                )
            )
        }
    }

    private fun setupClickListeners() {
        binding.layoutTopBar.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.layoutActionButtons.apply {
            btnCheck.setOnClickListener { handleCheckAnswer() }
            btnSkip.setOnClickListener { presenter.skipQuestion() }
            btnNext.setOnClickListener { presenter.nextQuestion() }
        }
    }

    private fun loadQuestions() {
        presenter.loadQuestions(
            categoryId = args.categoryId,
            difficulty = Difficulty.valueOf(args.difficulty.uppercase())
        )
    }

    override fun onRetryConnection() {
        super.onRetryConnection()
        loadQuestions()
    }

    private fun handleAnswerClick(position: Int) {
        if (stateManager.selectedAnswerIndex == position) return

        stateManager.selectAnswer(position)
        uiManager.updateAnswerSelection(stateManager.currentAnswers, position)
        uiManager.enableCheckButton(true)
    }

    private fun handleCheckAnswer() {
        if (stateManager.hasSelection()) {
            presenter.submitAnswer(stateManager.selectedAnswerIndex)
        }
    }

    override fun displayQuestion(
        question: TriviaQuestion,
        questionNumber: Int,
        totalQuestions: Int
    ) {
        uiManager.displayQuestion(question, questionNumber, totalQuestions)

        val answers = question.getAllAnswers()
        stateManager.setCurrentAnswers(answers)
        uiManager.displayAnswerOptions(answers)

        stateManager.resetSelection()
        uiManager.resetForNewQuestion()
        animationManager.clearFloatingTags()
        timerManager.start()
    }

    override fun showCorrectAnswer(correctAnswerIndex: Int, isCorrect: Boolean) {
        timerManager.stop()

        uiManager.showCorrectAnswer(
            stateManager.currentAnswers,
            correctAnswerIndex,
            stateManager.selectedAnswerIndex,
            isCorrect
        )

        animationManager.showFloatingTags(isCorrect)

        if (!isCorrect && stateManager.hasSelection()) {
            animationManager.animateLifeLoss(binding.layoutTopBar.tvLives)
        }
        uiManager.disableAnswerButtons()
        uiManager.showNextButton()
    }

    override fun navigateToResults(
        correctAnswers: Int,
        incorrectAnswers: Int,
        skippedAnswers: Int,
        totalTime: Long
    ) {
        val action = GameFragmentDirections.actionGameToResult(
            categoryName = args.categoryName,
            correctAnswers = correctAnswers,
            incorrectAnswers = incorrectAnswers,
            skippedAnswers = skippedAnswers,
            totalTime = totalTime
        )
        findNavController().navigate(action)
    }

    override fun updateLives(lives: Int) {
        uiManager.updateLives(lives)
    }

    override fun showOutOfLives() {
        timerManager.stop()
        uiManager.disableAnswerButtons()
        showBuyLifeDialog()
    }

    override fun showNoConnection() {
        timerManager.stop()
        super.showNoConnection()
    }

    private fun showBuyLifeDialog() {
        if (buyLifeDialog?.isAdded == true) return

        buyLifeDialog = BuyLifeDialog.newInstance()
        buyLifeDialog?.setOnLifePurchasedListener { _, _ ->
            presenter.refreshLives()
            enableAnswersAfterLifePurchase()
        }

        buyLifeDialog?.show(childFragmentManager, BuyLifeDialog.TAG)
    }

    private fun enableAnswersAfterLifePurchase() {
        uiManager.displayAnswerOptions(stateManager.currentAnswers)
        uiManager.enableAnswersAfterLifePurchase(stateManager.hasSelection())
        timerManager.start()
    }

    override fun onDestroyView() {
        timerManager.stop()
        animationManager.clearFloatingTags()
        buyLifeDialog = null
        super.onDestroyView()
    }
}