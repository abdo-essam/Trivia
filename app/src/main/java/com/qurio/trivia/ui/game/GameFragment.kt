package com.qurio.trivia.ui.game

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
import com.qurio.trivia.base.BaseFragment
import com.qurio.trivia.data.model.Difficulty
import com.qurio.trivia.data.model.TriviaQuestion
import com.qurio.trivia.databinding.FragmentGameBinding
import com.qurio.trivia.databinding.LayoutAnswerOptionBinding
import com.qurio.trivia.ui.dialogs.NoConnectionDialogFragment
import com.qurio.trivia.utils.Constants
import javax.inject.Inject
import kotlin.random.Random

class GameFragment : BaseFragment<FragmentGameBinding, GamePresenter>(),
    GameView, NoConnectionDialogFragment.RetryListener {

    @Inject
    override lateinit var presenter: GamePresenter

    override val binding: FragmentGameBinding by lazy {
        FragmentGameBinding.inflate(layoutInflater)
    }

    private val args: GameFragmentArgs by navArgs()
    private var countDownTimer: CountDownTimer? = null
    private var selectedAnswerIndex = -1

    private lateinit var answerOption1: LayoutAnswerOptionBinding
    private lateinit var answerOption2: LayoutAnswerOptionBinding
    private lateinit var answerOption3: LayoutAnswerOptionBinding
    private lateinit var answerOption4: LayoutAnswerOptionBinding

    private val answerOptions: List<LayoutAnswerOptionBinding> by lazy {
        listOf(answerOption1, answerOption2, answerOption3, answerOption4)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        return binding.root
    }

    override fun setupViews() {
        initializeAnswerOptions()
        setupClickListeners()
        loadQuestions()
    }

    private fun initializeAnswerOptions() {
        answerOption1 = LayoutAnswerOptionBinding.bind(binding.answerOption1.root)
        answerOption2 = LayoutAnswerOptionBinding.bind(binding.answerOption2.root)
        answerOption3 = LayoutAnswerOptionBinding.bind(binding.answerOption3.root)
        answerOption4 = LayoutAnswerOptionBinding.bind(binding.answerOption4.root)
    }

    private fun setupClickListeners() {
        binding.layoutTopBar.btnBack.setOnClickListener { navigateBack() }
        binding.actionButtonsContainer.btnCheck.setOnClickListener { handleCheckAnswer() }
        binding.actionButtonsContainer.btnSkip.setOnClickListener { handleSkipQuestion() }
        binding.actionButtonsContainer.btnNext.setOnClickListener { handleNextQuestion() }

        answerOptions.forEachIndexed { index, option ->
            option.btnAnswer.setOnClickListener { selectAnswer(index) }
        }
    }

    private fun loadQuestions() {
        presenter.loadQuestions(
            args.categoryId,
            Difficulty.valueOf(args.difficulty.uppercase())
        )
    }

    private fun navigateBack() {
        findNavController().navigateUp()
    }

    private fun handleCheckAnswer() {
        if (selectedAnswerIndex != -1) {
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
        updateAnswerStates()
        binding.actionButtonsContainer.btnCheck.isEnabled = true
    }

    private fun updateAnswerStates() {
        answerOptions.forEachIndexed { index, option ->
            if (index == selectedAnswerIndex) {
                option.answerContainer.setBackgroundResource(R.drawable.bg_answer_option_selected)
            } else {
                option.answerContainer.setBackgroundResource(R.drawable.bg_answer_option)
            }
        }
    }

    override fun displayQuestion(
        question: TriviaQuestion,
        questionNumber: Int,
        totalQuestions: Int
    ) {
        updateQuestionCounter(questionNumber, totalQuestions)
        updateQuestionText(question.question)
        hideQuestionImage()
        updateAnswerTexts(question.getAllAnswers())
        resetUIState()
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

    private fun updateQuestionCounter(questionNumber: Int, totalQuestions: Int) {
        binding.questionCounterContainer.tvQuestionCounter.text =
            getString(R.string.question_counter, questionNumber, totalQuestions)
    }

    private fun updateQuestionText(questionText: String) {
        binding.cardQuestion.tvQuestion.text = questionText
    }

    private fun hideQuestionImage() {
        binding.cardQuestion.cardImage.isVisible = false
        binding.cardQuestion.tvImageCaption.isVisible = false
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
            .into(binding.cardQuestion.ivQuestionImage)
    }

    private fun updateAnswerTexts(answers: List<String>) {
        answerOptions.forEachIndexed { index, option ->
            option.btnAnswer.text = answers.getOrNull(index) ?: ""
        }
    }

    private fun resetUIState() {
        selectedAnswerIndex = -1
        resetAnswerButtons()
        resetActionButtons()
        clearFloatingTags()
    }

    override fun showCorrectAnswer(correctAnswerIndex: Int, isCorrect: Boolean) {
        stopTimer()
        highlightCorrectAnswer(correctAnswerIndex)

        if (selectedAnswerIndex == -1) {
            // User didn't select any answer (time up or skipped)
            showFloatingTags(false)
        } else if (!isCorrect) {
            highlightWrongAnswer(selectedAnswerIndex)
            showFloatingTags(false)
        } else {
            showFloatingTags(true)
        }

        disableAllAnswers()
        showNextButton()
    }

    private fun highlightCorrectAnswer(index: Int) {
        answerOptions[index].answerContainer.setBackgroundResource(
            R.drawable.bg_answer_option_correct
        )
    }

    private fun highlightWrongAnswer(index: Int) {
        answerOptions[index].answerContainer.setBackgroundResource(
            R.drawable.bg_answer_option_wrong
        )
    }

    private fun disableAllAnswers() {
        answerOptions.forEach { it.btnAnswer.isEnabled = false }
        binding.actionButtonsContainer.btnCheck.isEnabled = false
        binding.actionButtonsContainer.btnSkip.isEnabled = false
    }

    private fun showNextButton() {
        binding.actionButtonsContainer.apply {
            btnCheck.isVisible = false
            btnSkip.isVisible = false
            btnNext.isVisible = true
        }
    }

    override fun navigateToResults(
        correctAnswers: Int,
        incorrectAnswers: Int,
        skippedAnswers: Int,
        totalTime: Long
    ) {
        val action = GameFragmentDirections.actionGameToResult(
            args.categoryName,
            correctAnswers,
            incorrectAnswers,
            skippedAnswers,
            totalTime
        )
        findNavController().navigate(action)
    }

    override fun updateStats(lives: Int, coins: Int) {
        binding.layoutTopBar.tvLives.text = lives.toString()
    }

    // ==================== Timer ====================

    private fun startQuestionTimer() {
        val timeLimit = Constants.QUESTION_TIME_LIMIT

        binding.timerContainer.progressTimer.apply {
            max = timeLimit.toInt()
            progress = timeLimit.toInt()
        }

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(timeLimit, 100) {
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

        val colorRes = if (seconds <= 10) R.color.red else R.color.orange
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

    // ==================== Reset UI ====================

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

    private fun clearFloatingTags() {
        binding.floatingTagsContainer.removeAllViews()
    }

// ==================== Floating Tags (18dp Size) ====================

    private fun showFloatingTags(isCorrect: Boolean) {
        val tagCount = Random.nextInt(5, 9)

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

        val params = view.layoutParams as FrameLayout.LayoutParams

        params.leftMargin = Random.nextInt(50, maxOf(containerWidth - 150, 51))
        params.topMargin = Random.nextInt(100, maxOf(containerHeight / 2, 101))

        view.layoutParams = params
    }

    private fun animateTag(view: View) {
        val startDelay = Random.nextLong(0, 300)

        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
        val translateY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0f, -150f)

        ObjectAnimator.ofPropertyValuesHolder(view, alpha, translateY).apply {
            duration = 1000
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
            .setDuration(400)
            .setStartDelay(300)
            .withEndAction {
                (view.parent as? ViewGroup)?.removeView(view)
            }
            .start()
    }


    // ==================== Lifecycle ====================

    override fun onRetryClicked() {
        loadQuestions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopTimer()
        clearFloatingTags()
    }
}