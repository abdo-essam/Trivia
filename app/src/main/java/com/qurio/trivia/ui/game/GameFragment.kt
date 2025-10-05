package com.qurio.trivia.ui.game

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.base.BaseFragment
import com.qurio.trivia.data.model.TriviaQuestion
import com.qurio.trivia.databinding.FragmentGameBinding
import com.qurio.trivia.ui.dialogs.NoConnectionDialogFragment
import com.qurio.trivia.utils.Constants
import javax.inject.Inject

class GameFragment : BaseFragment<FragmentGameBinding, GamePresenter>(), GameView, NoConnectionDialogFragment.RetryListener {

    @Inject
    override lateinit var presenter: GamePresenter

    override val binding: FragmentGameBinding by lazy {
        FragmentGameBinding.inflate(layoutInflater)
    }

    private val args: GameFragmentArgs by navArgs()
    private var countDownTimer: CountDownTimer? = null
    private var selectedAnswerIndex = -1
    private var currentQuestionIndex = 0
    private var totalQuestions = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        return binding.root
    }

    override fun setupViews() {
        binding.btnCheck.setOnClickListener {
            if (selectedAnswerIndex != -1) {
                presenter.submitAnswer(selectedAnswerIndex)
            }
        }

        binding.btnSkip.setOnClickListener {
            presenter.skipQuestion()
        }

        setupAnswerButtons()
        presenter.loadQuestions(args.categoryId, args.difficulty)
    }

    private fun setupAnswerButtons() {
        val answerButtons = listOf(
            binding.btnAnswer1,
            binding.btnAnswer2,
            binding.btnAnswer3,
            binding.btnAnswer4
        )

        answerButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                selectAnswer(index)
            }
        }
    }

    private fun selectAnswer(index: Int) {
        selectedAnswerIndex = index

        // Reset all buttons
        val answerButtons = listOf(
            binding.btnAnswer1,
            binding.btnAnswer2,
            binding.btnAnswer3,
            binding.btnAnswer4
        )

        answerButtons.forEach { button ->
            button.backgroundTintList = android.content.res.ColorStateList.valueOf(
                requireContext().getColor(R.color.gray_dark)
            )
            button.setTextColor(requireContext().getColor(R.color.white))
        }

        // Highlight selected answer
        answerButtons[index].backgroundTintList = android.content.res.ColorStateList.valueOf(
            requireContext().getColor(R.color.blue_primary)
        )

        binding.btnCheck.isEnabled = true
    }

    override fun displayQuestion(question: TriviaQuestion, questionNumber: Int, totalQuestions: Int) {
        this.currentQuestionIndex = questionNumber - 1
        this.totalQuestions = totalQuestions

        binding.tvQuestionCounter.text = "Q $questionNumber/$totalQuestions"
        binding.tvQuestion.text = question.question

        val answers = question.getAllAnswers()
        binding.btnAnswer1.text = answers.getOrNull(0) ?: ""
        binding.btnAnswer2.text = answers.getOrNull(1) ?: ""
        binding.btnAnswer3.text = answers.getOrNull(2) ?: ""
        binding.btnAnswer4.text = answers.getOrNull(3) ?: ""

        // Reset selection
        selectedAnswerIndex = -1
        binding.btnCheck.isEnabled = false

        // Start timer
        startQuestionTimer()

        // Reset answer button styles
        resetAnswerButtons()
    }

    override fun displayQuestionWithImage(question: TriviaQuestion, imageUrl: String?, questionNumber: Int, totalQuestions: Int) {
        displayQuestion(question, questionNumber, totalQuestions)

        if (!imageUrl.isNullOrEmpty()) {
            binding.ivQuestionImage.visibility = View.VISIBLE
            binding.tvImageCaption.visibility = View.VISIBLE

            Glide.with(this)
                .load(imageUrl)
                //.placeholder(R.drawable.placeholder_question)
                .into(binding.ivQuestionImage)

            binding.tvImageCaption.text = "The image caption"
        } else {
            binding.ivQuestionImage.visibility = View.GONE
            binding.tvImageCaption.visibility = View.GONE
        }
    }

    override fun showCorrectAnswer(correctAnswerIndex: Int) {
        val answerButtons = listOf(
            binding.btnAnswer1,
            binding.btnAnswer2,
            binding.btnAnswer3,
            binding.btnAnswer4
        )

        // Show correct answer in green
        answerButtons[correctAnswerIndex].backgroundTintList = android.content.res.ColorStateList.valueOf(
            requireContext().getColor(R.color.green)
        )

        // Show wrong answer in red if user selected wrong
        if (selectedAnswerIndex != -1 && selectedAnswerIndex != correctAnswerIndex) {
            answerButtons[selectedAnswerIndex].backgroundTintList = android.content.res.ColorStateList.valueOf(
                requireContext().getColor(R.color.red)
            )
            showErrorIndicators()
        }

        // Disable all buttons
        answerButtons.forEach { it.isEnabled = false }
        binding.btnCheck.isEnabled = false
        binding.btnSkip.isEnabled = false

        // Show next button
        binding.btnNext.visibility = View.VISIBLE
        binding.btnNext.setOnClickListener {
            presenter.nextQuestion()
        }

        stopTimer()
    }

    override fun navigateToResults(correctAnswers: Int, incorrectAnswers: Int, skippedAnswers: Int, totalTime: Long) {
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
        binding.tvLives.text = lives.toString()
        binding.tvCoins.text = coins.toString()
    }

    private fun startQuestionTimer() {
        binding.progressTimer.max = Constants.QUESTION_TIME_LIMIT.toInt()
        binding.progressTimer.progress = Constants.QUESTION_TIME_LIMIT.toInt()

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(Constants.QUESTION_TIME_LIMIT, 100) {
            override fun onTick(millisUntilFinished: Long) {
                binding.progressTimer.progress = millisUntilFinished.toInt()

                val seconds = millisUntilFinished / 1000
                binding.tvTimer.text = "${seconds}s"

                // Change color when time is running out
                if (seconds <= 10) {
                    binding.progressTimer.progressTintList = android.content.res.ColorStateList.valueOf(
                        requireContext().getColor(R.color.red)
                    )
                } else {
                    binding.progressTimer.progressTintList = android.content.res.ColorStateList.valueOf(
                        requireContext().getColor(R.color.orange)
                    )
                }
            }

            override fun onFinish() {
                binding.tvTimer.text = "0s"
                binding.progressTimer.progress = 0
                presenter.timeUp()
            }
        }.start()
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
    }

    private fun resetAnswerButtons() {
        val answerButtons = listOf(
            binding.btnAnswer1,
            binding.btnAnswer2,
            binding.btnAnswer3,
            binding.btnAnswer4
        )

        answerButtons.forEach { button ->
            button.isEnabled = true
            button.backgroundTintList = android.content.res.ColorStateList.valueOf(
                requireContext().getColor(R.color.gray_dark)
            )
            button.setTextColor(requireContext().getColor(R.color.white))
        }

        binding.btnNext.visibility = View.GONE
        binding.btnCheck.isEnabled = false
        binding.btnSkip.isEnabled = true

        hideErrorIndicators()
    }

    private fun showErrorIndicators() {
        // Add floating error indicators around wrong answer
        val errorViews = listOf(
            binding.ivError1,
            binding.ivError2,
            binding.ivError3,
            binding.ivError4
        )

        errorViews.forEach { it.visibility = View.VISIBLE }
    }

    private fun hideErrorIndicators() {
        val errorViews = listOf(
            binding.ivError1,
            binding.ivError2,
            binding.ivError3,
            binding.ivError4
        )

        errorViews.forEach { it.visibility = View.GONE }
    }

    override fun onRetryClicked() {
        presenter.loadQuestions(args.categoryId, args.difficulty)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopTimer()
    }
}