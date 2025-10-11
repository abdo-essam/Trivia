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
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.presentation.base.BaseFragment
import com.qurio.trivia.data.model.Difficulty
import com.qurio.trivia.data.model.TriviaQuestion
import com.qurio.trivia.databinding.FragmentGameBinding
import com.qurio.trivia.databinding.LayoutAnswerOptionBinding
import com.qurio.trivia.presentation.ui.dialogs.NoConnectionDialogFragment
import com.qurio.trivia.utils.Constants
import javax.inject.Inject
import kotlin.random.Random

class GameFragment : BaseFragment<FragmentGameBinding, GameView, GamePresenter>(),
    GameView, NoConnectionDialogFragment.RetryListener {

    @Inject
    lateinit var gamePresenter: GamePresenter

    private val args: GameFragmentArgs by navArgs()

    // ========== UI State ==========

    private var countDownTimer: CountDownTimer? = null
    private var selectedAnswerIndex = NO_SELECTION

    // ========== Answer Option Bindings ==========

    private lateinit var answerOption1: LayoutAnswerOptionBinding
    private lateinit var answerOption2: LayoutAnswerOptionBinding
    private lateinit var answerOption3: LayoutAnswerOptionBinding
    private lateinit var answerOption4: LayoutAnswerOptionBinding

    private val answerOptions: List<LayoutAnswerOptionBinding> by lazy {
        listOf(answerOption1, answerOption2, answerOption3, answerOption4)
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
        initializeAnswerOptions()
        setupClickListeners()
        loadQuestions()
    }

    // ========== Initialization ==========

    private fun initializeAnswerOptions() {
        answerOption1 = LayoutAnswerOptionBinding.bind(binding.answerOption1.root)
        answerOption2 = LayoutAnswerOptionBinding.bind(binding.answerOption2.root)
        answerOption3 = LayoutAnswerOptionBinding.bind(binding.answerOption3.root)
        answerOption4 = LayoutAnswerOptionBinding.bind(binding.answerOption4.root)
    }

    private fun setupClickListeners() {
        binding.layoutTopBar.btnBack.setOnClickListener {
            navigateBack()
        }

        binding.actionButtonsContainer.apply {
            btnCheck.setOnClickListener { handleCheckAnswer() }
            btnSkip.setOnClickListener { handleSkipQuestion() }
            btnNext.setOnClickListener { handleNextQuestion() }
        }

        answerOptions.forEachIndexed { index, option ->
            option.btnAnswer.setOnClickListener { selectAnswer(index) }
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

    private fun selectAnswer(index: Int) {
        if (selectedAnswerIndex == index) return

        selectedAnswerIndex = index
        updateAnswerSelectionUI()
        binding.actionButtonsContainer.btnCheck.isEnabled = true
    }

    // ========== GameView Implementation ==========

    override fun displayQuestion(
        question: TriviaQuestion,
        questionNumber: Int,
        totalQuestions: Int
    ) {
        updateQuestionHeader(questionNumber, totalQuestions)
        updateQuestionContent(question)
        hideQuestionImage()
        updateAnswerOptions(question.getAllAnswers())
        resetUIForNewQuestion()
        startQuestionTimer()
    }

    override fun displayQuestionWithImage(
        question: TriviaQuestion,
        imageUrl: String?,
        questionNumber: Int,
        totalQuestions: Int
    ) {
        displayQuestion(question, questionNumber, totalQuestions)

        if (!imageUrl.isNullOrEmpty()) {
            showQuestionImage(imageUrl, question.category)
        }
    }

    override fun showCorrectAnswer(correctAnswerIndex: Int, isCorrect: Boolean) {
        stopTimer()
        highlightCorrectAnswer(correctAnswerIndex)

        when {
            selectedAnswerIndex == NO_SELECTION -> {
                // User didn't select any answer (time up or skipped)
                showFloatingTags(isCorrect = false)
            }
            !isCorrect -> {
                highlightWrongAnswer(selectedAnswerIndex)
                showFloatingTags(isCorrect = false)
            }
            else -> {
                showFloatingTags(isCorrect = true)
            }
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
     /*   val action = GameFragmentDirections.actionGameToResult(
            categoryName = args.categoryName,
            correctAnswers = correctAnswers,
            incorrectAnswers = incorrectAnswers,
            skippedAnswers = skippedAnswers,
            totalTime = totalTime
        )
        findNavController().navigate(action)*/
    }

    override fun updateStats(lives: Int, coins: Int) {
        binding.layoutTopBar.tvLives.text = lives.toString()
       // binding.layoutTopBar.tvCoins.text = coins.toString()
    }

    // ========== Question UI Updates ==========

    private fun updateQuestionHeader(questionNumber: Int, totalQuestions: Int) {
        binding.questionCounterContainer.tvQuestionCounter.text =
            getString(R.string.question_counter, questionNumber, totalQuestions)
    }

    private fun updateQuestionContent(question: TriviaQuestion) {
        binding.cardQuestion.tvQuestion.text = question.question
    }

    private fun hideQuestionImage() {
        binding.cardQuestion.apply {
            cardImage.isVisible = false
            tvImageCaption.isVisible = false
        }
    }

    private fun showQuestionImage(imageUrl: String, caption: String) {
        binding.cardQuestion.apply {
            cardImage.isVisible = true
            tvImageCaption.isVisible = true
            tvImageCaption.text = caption
        }

        Glide.with(this)
            .load(imageUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_qurio_logo)
            .error(R.drawable.ic_close)
            .into(binding.cardQuestion.ivQuestionImage)
    }

    private fun updateAnswerOptions(answers: List<String>) {
        answerOptions.forEachIndexed { index, option ->
            option.btnAnswer.text = answers.getOrNull(index) ?: ""
        }
    }

    // ========== Answer UI Updates ==========

    private fun updateAnswerSelectionUI() {
        answerOptions.forEachIndexed { index, option ->
            val backgroundRes = if (index == selectedAnswerIndex) {
                R.drawable.bg_answer_option_selected
            } else {
                R.drawable.bg_answer_option
            }
            option.answerContainer.setBackgroundResource(backgroundRes)
        }
    }

    private fun highlightCorrectAnswer(index: Int) {
        answerOptions.getOrNull(index)?.answerContainer?.setBackgroundResource(
            R.drawable.bg_answer_option_correct
        )
    }

    private fun highlightWrongAnswer(index: Int) {
        answerOptions.getOrNull(index)?.answerContainer?.setBackgroundResource(
            R.drawable.bg_answer_option_wrong
        )
    }

    private fun disableAllAnswers() {
        answerOptions.forEach { it.btnAnswer.isEnabled = false }

        binding.actionButtonsContainer.apply {
            btnCheck.isEnabled = false
            btnSkip.isEnabled = false
        }
    }

    private fun showNextButton() {
        binding.actionButtonsContainer.apply {
            btnCheck.isVisible = false
            btnSkip.isVisible = false
            btnNext.isVisible = true
        }
    }

    // ========== UI Reset ==========

    private fun resetUIForNewQuestion() {
        selectedAnswerIndex = NO_SELECTION
        resetAnswerButtons()
        resetActionButtons()
        clearFloatingTags()
    }

    private fun resetAnswerButtons() {
        answerOptions.forEach { option ->
            option.btnAnswer.isEnabled = true
            option.answerContainer.setBackgroundResource(R.drawable.bg_answer_option)
        }
    }

    private fun resetActionButtons() {
        binding.actionButtonsContainer.apply {
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

        binding.timerContainer.progressTimer.apply {
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

        binding.timerContainer.apply {
            progressTimer.progress = millisUntilFinished.toInt()
            tvTimer.text = getString(R.string.timer_seconds, seconds)
        }

        val colorRes = if (seconds <= TIMER_WARNING_THRESHOLD) {
            R.color.red
        } else {
            R.color.orange
        }

        binding.timerContainer.progressTimer.progressTintList =
            ContextCompat.getColorStateList(requireContext(), colorRes)
    }

    private fun handleTimerFinish() {
        binding.timerContainer.apply {
            tvTimer.text = getString(R.string.timer_seconds, 0)
            progressTimer.progress = 0
        }
        presenter.timeUp()
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
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
        animateTag(imageView)
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

    private fun animateTag(view: View) {
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

    // ========== NoConnectionDialogFragment.RetryListener ==========

    override fun onRetryClicked() {
        loadQuestions()
    }

    // ========== Lifecycle ==========

    override fun onDestroyView() {
        stopTimer()
        clearFloatingTags()
        super.onDestroyView()
    }

    // ========== Constants ==========

    companion object {
        private const val NO_SELECTION = -1

        // Timer
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