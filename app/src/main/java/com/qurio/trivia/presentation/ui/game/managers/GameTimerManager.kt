package com.qurio.trivia.presentation.ui.game.managers

import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import com.qurio.trivia.R
import com.qurio.trivia.databinding.FragmentGameBinding
import com.qurio.trivia.utils.Constants

class GameTimerManager(
    private val binding: FragmentGameBinding,
    private val onTimerFinish: () -> Unit
) {
    private var countDownTimer: CountDownTimer? = null

    fun start() {
        val timeLimit = Constants.QUESTION_TIME_LIMIT

        binding.layoutTimer.progressTimer.apply {
            max = timeLimit.toInt()
            progress = timeLimit.toInt()
        }

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(timeLimit, TIMER_TICK_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                updateUI(millisUntilFinished)
            }

            override fun onFinish() {
                handleFinish()
            }
        }.start()
    }

    private fun updateUI(millisUntilFinished: Long) {
        val seconds = millisUntilFinished / 1000

        binding.layoutTimer.apply {
            progressTimer.progress = millisUntilFinished.toInt()
            tvTimerText.text = binding.root.context.getString(
                R.string.timer_seconds_format,
                seconds
            )

            val colorRes = if (seconds <= TIMER_WARNING_THRESHOLD) {
                R.color.red
            } else {
                R.color.orange
            }

            progressTimer.progressTintList = ContextCompat.getColorStateList(
                binding.root.context,
                colorRes
            )
        }
    }

    private fun handleFinish() {
        binding.layoutTimer.apply {
            tvTimerText.text = binding.root.context.getString(R.string.timer_seconds_format, 0)
            progressTimer.progress = 0
        }
        onTimerFinish()
    }

    fun stop() {
        countDownTimer?.cancel()
        countDownTimer = null
    }

    companion object {
        private const val TIMER_TICK_INTERVAL = 100L
        private const val TIMER_WARNING_THRESHOLD = 10L
    }
}