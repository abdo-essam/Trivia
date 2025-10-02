package com.qurio.trivia.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.base.BaseFragment
import com.qurio.trivia.data.model.Category
import com.qurio.trivia.data.model.GameResult
import com.qurio.trivia.data.model.UserProgress
import com.qurio.trivia.databinding.FragmentHomeBinding
import com.qurio.trivia.ui.adapters.CategoryAdapter
import com.qurio.trivia.ui.adapters.LastGamesAdapter
import com.qurio.trivia.ui.dialogs.SettingsDialogFragment
import javax.inject.Inject

class HomeFragment : BaseFragment<FragmentHomeBinding, HomePresenter>(), HomeView {

    @Inject
    override lateinit var presenter: HomePresenter

    override val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var lastGamesAdapter: LastGamesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        return binding.root
    }

    override fun setupViews() {
        setupCategoriesRecyclerView()
        setupLastGamesRecyclerView()

        binding.btnSettings.setOnClickListener {
            showSettings()
        }

        binding.tvAllLastGames.setOnClickListener {
            navigateToAllLastGames()
        }

        // Initialize fake data first (for testing)
        presenter.initializeData()

        // Load data
        presenter.loadUserProgress()
        presenter.loadCategories()
        presenter.loadLastGames(3) // Show only 3 games in home screen
    }

    override fun setupObservers() {
        // No observers needed for MVP
    }

    private fun setupCategoriesRecyclerView() {
        categoryAdapter = CategoryAdapter { category ->
            navigateToCharacterSelection(category)
        }

        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = categoryAdapter
            setHasFixedSize(true)

        }
        // Load categories immediately after setup
        presenter.loadCategories()
    }

    private fun setupLastGamesRecyclerView() {
        lastGamesAdapter = LastGamesAdapter { gameResult ->
            // Handle click on game result if needed
            // Could show game details or replay
        }

        binding.rvLastGames.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = lastGamesAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun showSettings() {
        val settingsDialog = SettingsDialogFragment()
        settingsDialog.show(childFragmentManager, "settings")
    }

    private fun navigateToCharacterSelection(category: Category) {
        val action = HomeFragmentDirections.actionHomeToCharacterSelection(
            categoryId = category.id,
            categoryName = category.displayName
        )
        findNavController().navigate(action)
    }

    private fun navigateToAllLastGames() {
        // TODO: Navigate to all last games screen
        // val action = HomeFragmentDirections.actionHomeToLastGames()
        // findNavController().navigate(action)
    }

    @SuppressLint("SetTextI18n")
    override fun displayUserProgress(userProgress: UserProgress) {
        binding.tvLives.text = userProgress.lives.toString()
        binding.tvCoins.text = userProgress.totalCoins.toString()

        binding.tvWelcome.text = "Welcome Qurio explorer"
        binding.tvCharacterName.text = getCharacterDisplayName(userProgress.selectedCharacter)

        val characterImageRes = getCharacterImageRes(userProgress.selectedCharacter)
        binding.ivCharacter.setImageResource(characterImageRes)

        updateStreakDisplay(userProgress)
    }

    private fun updateStreakDisplay(userProgress: UserProgress) {
        val streak = userProgress.currentStreak

        if (streak == 0) {
            binding.layoutStreak.tvStreakTitle.text = "0 day streak, start make a series"
            binding.layoutStreak.tvStreakSubtitle.text = "Every day count!"
        } else {
            binding.layoutStreak.tvStreakTitle.text = "$streak day streak, make a big series"
            binding.layoutStreak.tvStreakSubtitle.text = "KEEP IT UP!"
        }

        val streakDays = userProgress.streakDays.split(",").mapNotNull { it.toIntOrNull() }
        val fireViews = listOf(
            binding.layoutStreak.ivFireSunday,
            binding.layoutStreak.ivFireMonday,
            binding.layoutStreak.ivFireTuesday,
            binding.layoutStreak.ivFireWednesday,
            binding.layoutStreak.ivFireThursday,
            binding.layoutStreak.ivFireFriday,
            binding.layoutStreak.ivFireSaturday
        )

        fireViews.forEachIndexed { index, imageView ->
            imageView.visibility = if (streakDays.contains(index)) View.VISIBLE else View.GONE
        }
    }

    override fun displayCategories(categories: List<Category>) {
        Log.d("HomeFragment", "Categories loaded: ${categories.size}")
        categoryAdapter.submitList(categories)
    }

    override fun displayLastGames(games: List<GameResult>) {
        Log.d("HomeFragment", "Last games loaded: ${games.size}")
        lastGamesAdapter.submitList(games)

        // Show/hide last games section based on availability
        if (games.isEmpty()) {
            binding.layoutLastGamesHeader.visibility = View.GONE
            binding.rvLastGames.visibility = View.GONE
        } else {
            binding.layoutLastGamesHeader.visibility = View.VISIBLE
            binding.rvLastGames.visibility = View.VISIBLE
        }
    }

    private fun getCharacterDisplayName(characterName: String): String {
        return characterName.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }

    private fun getCharacterImageRes(characterName: String): Int {
        return when (characterName) {
            "rika" -> R.drawable.character_rika
            "kaiyo" -> R.drawable.character_kaiyo
            "mimi" -> R.drawable.character_mimi
            "yoru" -> R.drawable.character_yoru
            "kuro" -> R.drawable.character_kuro
            "miko" -> R.drawable.character_miko
            "aori" -> R.drawable.character_aori
            "nara" -> R.drawable.character_nara
            "renji" -> R.drawable.character_renji
            else -> R.drawable.character_rika
        }
    }
}