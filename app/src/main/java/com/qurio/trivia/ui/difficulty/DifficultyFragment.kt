package com.qurio.trivia.ui.difficulty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.base.BaseFragment
import com.qurio.trivia.data.model.Difficulty
import com.qurio.trivia.databinding.FragmentDifficultyBinding
import javax.inject.Inject

class DifficultyFragment : BaseFragment<FragmentDifficultyBinding, DifficultyPresenter>(), DifficultyView {

    @Inject
    override lateinit var presenter: DifficultyPresenter

    override val binding: FragmentDifficultyBinding by lazy {
        FragmentDifficultyBinding.inflate(layoutInflater)
    }

    private val args: DifficultyFragmentArgs by navArgs()
    private var selectedDifficulty: Difficulty = Difficulty.MEDIUM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        return binding.root
    }

    override fun setupViews() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnStartGame.setOnClickListener {
            presenter.startGame(args.categoryId, selectedDifficulty.value)
        }

        setupDifficultySelection()

        // Default to Medium difficulty
        selectDifficulty(Difficulty.MEDIUM)
    }

    override fun setupObservers() {
        // No observers needed
    }

    private fun setupDifficultySelection() {
        binding.btnEasy.setOnClickListener { selectDifficulty(Difficulty.EASY) }
        binding.btnMedium.setOnClickListener { selectDifficulty(Difficulty.MEDIUM) }
        binding.btnHard.setOnClickListener { selectDifficulty(Difficulty.HARD) }
    }

    private fun selectDifficulty(difficulty: Difficulty) {
        selectedDifficulty = difficulty

        // Reset all buttons
        binding.btnEasy.isSelected = false
        binding.btnMedium.isSelected = false
        binding.btnHard.isSelected = false

        // Select current difficulty
        when (difficulty) {
            Difficulty.EASY -> binding.btnEasy.isSelected = true
            Difficulty.MEDIUM -> binding.btnMedium.isSelected = true
            Difficulty.HARD -> binding.btnHard.isSelected = true
        }

        updateButtonStyles()
    }

    private fun updateButtonStyles() {
        listOf(binding.btnEasy, binding.btnMedium, binding.btnHard).forEach { button ->
            if (button.isSelected) {
                button.backgroundTintList = android.content.res.ColorStateList.valueOf(
                    requireContext().getColor(com.qurio.trivia.R.color.blue_primary)
                )
                button.setTextColor(requireContext().getColor(com.qurio.trivia.R.color.white))
            } else {
                button.backgroundTintList = android.content.res.ColorStateList.valueOf(
                    requireContext().getColor(com.qurio.trivia.R.color.gray_dark)
                )
                button.setTextColor(requireContext().getColor(com.qurio.trivia.R.color.gray_light))
            }
        }
    }

    override fun navigateToGame() {
        val action = DifficultyFragmentDirections.actionDifficultyToGame(
            args.categoryId,
            args.categoryName,
            selectedDifficulty.value
        )
        findNavController().navigate(action)
    }

    override fun showNotEnoughLives() {
        // Navigate to buy lives screen
        val action = DifficultyFragmentDirections.actionDifficultyToBuyLife()
        findNavController().navigate(action)
    }
}