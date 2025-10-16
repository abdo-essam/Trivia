package com.qurio.trivia.presentation.ui.game

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.data.model.TriviaQuestion
import com.qurio.trivia.databinding.FragmentGameBinding
import com.qurio.trivia.domain.model.Difficulty
import com.qurio.trivia.presentation.base.BaseFragment
import com.qurio.trivia.presentation.ui.dialogs.buylife.BuyLifeDialog
import com.qurio.trivia.presentation.ui.game.adapter.AnswerItemDecoration
import com.qurio.trivia.presentation.ui.game.adapter.AnswerOption
import com.qurio.trivia.presentation.ui.game.adapter.AnswerOptionAdapter
import com.qurio.trivia.presentation.ui.game.adapter.AnswerState
import com.qurio.trivia.utils.Constants
import javax.inject.Inject
import kotlin.random.Random

class GameFragment : BaseFragment<FragmentGameBinding, GameView, GamePresenter>(),
    GameView {

    @Inject
    lateinit var gamePresenter: GamePresenter

    private val args: GameFragmentArgs by navArgs()

    // ========== State ==========
    private var countDownTimer: CountDownTimer? = null
    private var selectedAnswerIndex = NO_SELECTION
    private var buyLifeDialog: BuyLifeDialog? = null
    private var currentAnswers = listOf<String>()

    // ========== Adapter ==========
    private val answerAdapter by lazy {
        AnswerOptionAdapter { position ->
            handleAnswerClick(position)
        }
    }

    // ========== BaseFragment Implementation ==========

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
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
        setupRecyclerView()
        setupClickListeners()
        loadQuestions()
    }

    // ========== Setup ==========

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
            navigateBack()
        }

        binding.layoutActionButtons.apply {
            btnCheck.setOnClickListener { handleCheckAnswer() }
            btnSkip.setOnClickListener { handleSkipQuestion() }
            btnNext.setOnClickListener { handleNextQuestion() }
        }
    }

    // ========== Data Loading ==========

    private fun loadQuestions() {
        presenter.loadQuestions(
            categoryId = args.categoryId,
            difficulty = Difficulty.valueOf(args.difficulty.uppercase())
        )
    }

    // ========== User Actions ==========

    private fun navigateBack() {
        findNavController().navigateUp()
    }

    private fun handleAnswerClick(position: Int) {
        if (selectedAnswerIndex == position) return

        selectedAnswerIndex = position
        updateAnswerSelection()
        binding.layoutActionButtons.btnCheck.isEnabled = true
    }

    private fun handleCheckAnswer() {
        if (selectedAnswerIndex != NO_SELECTION) {
            presenter.submitAnswer(selectedAnswerIndex)
        }
    }

    private fun handleSkipQuestion() {
        presenter.skipQuestion()
    }

    private fun handleNextQuestion() {
        presenter.nextQuestion()
    }

    // ========== GameView Implementation ==========

        override fun displayQuestion(
            question: TriviaQuestion,
        questionNumber: Int,
        totalQuestions: Int
    ) {
        // Update question header
        binding.layoutQuestion.root.findViewById<android.widget.TextView>(R.id.tv_question_counter)?.text =
            getString(R.string.question_counter, questionNumber, totalQuestions)

        // Update question text with HTML decoding
        val decodedQuestion = HtmlCompat.fromHtml(
            question.question,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        binding.layoutQuestion.root.findViewById<android.widget.TextView>(R.id.tv_question)?.text =
            decodedQuestion

        // Store and display answers
        currentAnswers = question.getAllAnswers()
        displayAnswerOptions(currentAnswers)

        // Reset UI
        resetUIForNewQuestion()
        startQuestionTimer()
    }

    override fun showCorrectAnswer(correctAnswerIndex: Int, isCorrect: Boolean) {
        stopTimer()

        val updatedOptions = currentAnswers.mapIndexed { index, text ->
            when {
                index == correctAnswerIndex -> {
                    AnswerOption(text, AnswerState.CORRECT, false)
                }
                index == selectedAnswerIndex && !isCorrect -> {
                    AnswerOption(text, AnswerState.INCORRECT, false)
                }
                else -> {
                    AnswerOption(text, AnswerState.DEFAULT, false)
                }
            }
        }

        answerAdapter.submitList(updatedOptions)

        // Show floating tags
        showFloatingTags(isCorrect)

        if (!isCorrect && selectedAnswerIndex != NO_SELECTION) {
            animateLifeLoss()
        }

        disableAllAnswers()
        showNextButton()
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
        binding.layoutTopBar.tvLives.text = lives.toString()

        val colorRes = when {
            lives == 0 -> R.color.red
            lives <= 1 -> R.color.orange
            else -> R.color.white
        }

        binding.layoutTopBar.tvLives.setTextColor(
            ContextCompat.getColor(requireContext(), colorRes)
        )
    }

    override fun showOutOfLives() {
        stopTimer()
        disableAllAnswers()
        showBuyLifeDialog()
    }

    // ========== Answer Options UI ==========

    private fun displayAnswerOptions(answers: List<String>) {
        val options = answers.map { text ->
            AnswerOption(text, AnswerState.DEFAULT, true)
        }
        answerAdapter.submitList(options)
    }

    private fun updateAnswerSelection() {
        val updatedOptions = currentAnswers.mapIndexed { index, text ->
            val state = if (index == selectedAnswerIndex) {
                AnswerState.SELECTED
            } else {
                AnswerState.DEFAULT
            }
            AnswerOption(text, state, true)
        }
        answerAdapter.submitList(updatedOptions)
    }

    private fun disableAllAnswers() {
        binding.layoutActionButtons.apply {
            btnCheck.isEnabled = false
            btnSkip.isEnabled = false
        }
    }

    private fun showNextButton() {
        binding.layoutActionButtons.apply {
            btnCheck.isVisible = false
            btnSkip.isVisible = false
            btnNext.isVisible = true
        }
    }

    // ========== UI Reset ==========

    private fun resetUIForNewQuestion() {
        selectedAnswerIndex = NO_SELECTION
        resetActionButtons()
        clearFloatingTags()
    }

    private fun resetActionButtons() {
        binding.layoutActionButtons.apply {
            btnNext.isVisible = false
            btnCheck.isVisible = true
            btnSkip.isVisible = true
            btnCheck.isEnabled = false
            btnSkip.isEnabled = true
        }
    }

    // ========== Timer Management ==========

    private fun startQuestionTimer() {
        val timeLimit = Constants.QUESTION_TIME_LIMIT

        binding.layoutTimer.progressTimer.apply {
            max = timeLimit.toInt()
            progress = timeLimit.toInt()
        }

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(timeLimit, TIMER_TICK_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                updateTimerUI(millisUntilFinished)
            }

            override fun onFinish() {
                handleTimerFinish()
            }
        }.start()
    }

    private fun updateTimerUI(millisUntilFinished: Long) {
        val seconds = millisUntilFinished / 1000

        binding.layoutTimer.apply {
            progressTimer.progress = millisUntilFinished.toInt()
            tvTimerText.text = getString(R.string.timer_seconds_format, seconds)
        }

        val colorRes = if (seconds <= TIMER_WARNING_THRESHOLD) {
            R.color.red
        } else {
            R.color.orange
        }

        binding.layoutTimer.progressTimer.progressTintList =
            ContextCompat.getColorStateList(requireContext(), colorRes)
    }

    private fun handleTimerFinish() {
        binding.layoutTimer.apply {
            tvTimerText.text = getString(R.string.timer_seconds_format, 0)
            progressTimer.progress = 0
        }
        presenter.timeUp()
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
    }

    // ========== Life Loss Animation ==========

    private fun animateLifeLoss() {
        val livesView = binding.layoutTopBar.tvLives

        val shake = ObjectAnimator.ofFloat(
            livesView,
            View.TRANSLATION_X,
            0f, -25f, 25f, -25f, 25f, -15f, 15f, -5f, 5f, 0f
        ).apply {
            duration = 500
        }

        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.3f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.3f, 1f)
        val scale = ObjectAnimator.ofPropertyValuesHolder(livesView, scaleX, scaleY).apply {
            duration = 300
        }

        shake.start()
        scale.start()
    }

    // ========== Floating Tags Animation ==========

    private fun showFloatingTags(isCorrect: Boolean) {
        val tagCount = Random.nextInt(MIN_TAGS, MAX_TAGS + 1)

        repeat(tagCount) {
            createFloatingTag(isCorrect)
        }
    }

    private fun createFloatingTag(isCorrect: Boolean) {
        val tagSize = resources.getDimensionPixelSize(R.dimen.floating_tag_size)

        val imageView = ImageView(requireContext()).apply {
            setImageResource(
                if (isCorrect) R.drawable.ic_floating_tag_correct
                else R.drawable.ic_floating_tag_incorrect
            )
            layoutParams = FrameLayout.LayoutParams(tagSize, tagSize)
            alpha = 0f
        }

        positionTagRandomly(imageView)
        binding.floatingTagsContainer.addView(imageView)
        animateFloatingTag(imageView)
    }

    private fun positionTagRandomly(view: View) {
        val container = binding.floatingTagsContainer
        val containerWidth = container.width
        val containerHeight = container.height

        if (containerWidth <= 0 || containerHeight <= 0) return

        val params = view.layoutParams as FrameLayout.LayoutParams

        params.leftMargin = Random.nextInt(
            TAG_MARGIN_MIN,
            maxOf(containerWidth - TAG_MARGIN_MAX, TAG_MARGIN_MIN + 1)
        )
        params.topMargin = Random.nextInt(
            TAG_TOP_MARGIN,
            maxOf(containerHeight / 2, TAG_TOP_MARGIN + 1)
        )

        view.layoutParams = params
    }

    private fun animateFloatingTag(view: View) {
        val startDelay = Random.nextLong(0, TAG_ANIMATION_DELAY_MAX)

        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
        val translateY = PropertyValuesHolder.ofFloat(
            View.TRANSLATION_Y,
            0f,
            -TAG_TRANSLATION_Y
        )

        ObjectAnimator.ofPropertyValuesHolder(view, alpha, translateY).apply {
            duration = TAG_ANIMATION_DURATION
            this.startDelay = startDelay

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    fadeOutAndRemoveTag(view)
                }
            })

            start()
        }
    }

    private fun fadeOutAndRemoveTag(view: View) {
        view.animate()
            .alpha(0f)
            .setDuration(TAG_FADE_DURATION)
            .setStartDelay(TAG_FADE_DELAY)
            .withEndAction {
                (view.parent as? ViewGroup)?.removeView(view)
            }
            .start()
    }

    private fun clearFloatingTags() {
        binding.floatingTagsContainer.removeAllViews()
    }

    // ========== Buy Life Dialog ==========

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
        displayAnswerOptions(currentAnswers)

        binding.layoutActionButtons.apply {
            btnCheck.isEnabled = selectedAnswerIndex != NO_SELECTION
            btnSkip.isEnabled = true
        }

        startQuestionTimer()
    }

    // ========== Lifecycle ==========

    override fun onDestroyView() {
        stopTimer()
        clearFloatingTags()
        buyLifeDialog = null
        super.onDestroyView()
    }

    // ========== Constants ==========

    companion object {
        private const val NO_SELECTION = -1
        private const val TIMER_TICK_INTERVAL = 100L
        private const val TIMER_WARNING_THRESHOLD = 10L

        // Floating Tags
        private const val MIN_TAGS = 5
        private const val MAX_TAGS = 8
        private const val TAG_MARGIN_MIN = 50
        private const val TAG_MARGIN_MAX = 150
        private const val TAG_TOP_MARGIN = 100
        private const val TAG_TRANSLATION_Y = 150f
        private const val TAG_ANIMATION_DURATION = 1000L
        private const val TAG_ANIMATION_DELAY_MAX = 300L
        private const val TAG_FADE_DURATION = 400L
        private const val TAG_FADE_DELAY = 300L
    }
}