package com.qurio.trivia.ui.home

import android.os.Bundle
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

    private val categoryAdapter by lazy {
        CategoryAdapter(::navigateToCharacterSelection)
    }

    private val lastGamesAdapter by lazy {
        LastGamesAdapter { /* Handle game result click */ }
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
        setupCategoriesRecyclerView()
        setupLastGamesRecyclerView()
        setupClickListeners()
        loadInitialData()
    }

    override fun setupObservers() {
        // No observers needed for MVP
    }

    private fun setupCategoriesRecyclerView() {
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupLastGamesRecyclerView() {
        binding.rvLastGames.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = lastGamesAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupClickListeners() {
        binding.btnSettings.setOnClickListener {
            SettingsDialogFragment().show(childFragmentManager, "settings")
        }

        binding.tvAllLastGames.setOnClickListener {
            // TODO: Navigate to all last games
        }
    }

    private fun loadInitialData() {
        presenter.apply {
            initializeData()
            loadUserProgress()
            loadCategories()
            loadLastGames(3)
        }
    }

    override fun displayUserProgress(userProgress: UserProgress) {
        binding.apply {
            tvLives.text = userProgress.lives.toString()
            tvCoins.text = userProgress.totalCoins.toString()
            tvWelcome.text = getString(R.string.welcome_qurio_explorer)
            tvCharacterName.text = userProgress.selectedCharacter.capitalize()
            ivCharacter.setImageResource(getCharacterImageRes(userProgress.selectedCharacter))
        }
        updateStreakDisplay(userProgress)
    }

    private fun updateStreakDisplay(userProgress: UserProgress) {
        val streak = userProgress.currentStreak

        binding.layoutStreak.apply {
            if (streak == 0) {
                tvStreakTitle.text = getString(R.string.streak_start)
                tvStreakSubtitle.text = getString(R.string.every_day_count)
            } else {
                tvStreakTitle.text = getString(R.string.streak_count, streak)
                tvStreakSubtitle.text = getString(R.string.keep_it_up)
            }
        }

        updateStreakFireIcons(userProgress.streakDays)
    }

    private fun updateStreakFireIcons(streakDays: String) {
        val days = streakDays.split(",").mapNotNull { it.toIntOrNull() }.toSet()
        val fireViews = with(binding.layoutStreak) {
            listOf(
                ivFireSunday, ivFireMonday, ivFireTuesday, ivFireWednesday,
                ivFireThursday, ivFireFriday, ivFireSaturday
            )
        }

        fireViews.forEachIndexed { index, imageView ->
            imageView.visibility = if (index in days) View.VISIBLE else View.GONE
        }
    }

    override fun displayCategories(categories: List<Category>) {
        categoryAdapter.submitList(categories)
    }

    override fun displayLastGames(games: List<GameResult>) {
        lastGamesAdapter.submitList(games)

        val visibility = if (games.isEmpty()) View.GONE else View.VISIBLE
        binding.layoutLastGamesHeader.visibility = visibility
        binding.rvLastGames.visibility = visibility
    }

    private fun navigateToCharacterSelection(category: Category) {
        val action = HomeFragmentDirections.actionHomeToCharacterSelection(
            categoryId = category.id,
            categoryName = category.displayName
        )
        findNavController().navigate(action)
    }

    private fun getCharacterImageRes(characterName: String): Int = when (characterName) {
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

    private fun String.capitalize() = replaceFirstChar {
        if (it.isLowerCase()) it.titlecase() else it.toString()
    }
}