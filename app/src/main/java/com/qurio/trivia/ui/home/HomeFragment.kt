package com.qurio.trivia.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.base.BaseFragment
import com.qurio.trivia.data.model.Category
import com.qurio.trivia.data.model.GameResult
import com.qurio.trivia.data.model.UserProgress
import com.qurio.trivia.databinding.FragmentHomeBinding
import com.qurio.trivia.databinding.LayoutTopBarBinding
import com.qurio.trivia.ui.adapters.CategoryAdapter
import com.qurio.trivia.ui.adapters.LastGamesAdapter
import com.qurio.trivia.ui.dialogs.SettingsDialogFragment
import com.qurio.trivia.utils.extensions.capitalizeFirst
import com.qurio.trivia.utils.extensions.loadCharacterImage
import javax.inject.Inject

class HomeFragment : BaseFragment<FragmentHomeBinding, HomePresenter>(), HomeView {

    @Inject
    override lateinit var presenter: HomePresenter

    override val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    // Lazy initialization - will be created when first accessed
    private val topBarBinding: LayoutTopBarBinding by lazy {
        LayoutTopBarBinding.bind(binding.root.findViewById(R.id.layout_top_bar))
    }

    private val categoryAdapter by lazy {
        CategoryAdapter(::onCategoryClick)
    }

    private val lastGamesAdapter by lazy {
        LastGamesAdapter(::onGameResultClick)
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
        setupTopBar()
        setupRecyclerViews()
        setupClickListeners()
        loadData()
    }

    override fun setupObservers() {
        // MVP pattern - no observers needed
    }

    private fun setupTopBar() {
        topBarBinding.btnSettings.setOnClickListener { showSettingsDialog() }
    }

    private fun setupRecyclerViews() {
        with(binding) {
            rvCategories.apply {
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = categoryAdapter
                setHasFixedSize(true)
            }

            rvLastGames.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = lastGamesAdapter
                isNestedScrollingEnabled = false
            }
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            tvAllLastGames.setOnClickListener { navigateToAllGames() }
            tvAllCategories.setOnClickListener { navigateToAllCategories() }
        }
    }

    private fun loadData() {
        presenter.apply {
            initializeData()
            loadUserProgress()
            loadCategories()
            loadLastGames(LAST_GAMES_LIMIT)
        }
    }

    override fun displayUserProgress(userProgress: UserProgress) {
        // Update top bar
        with(topBarBinding) {
            tvWelcome.text = getString(R.string.welcome_qurio_explorer)
            tvCharacterName.text = userProgress.selectedCharacter.capitalizeFirst()
            ivCharacter.loadCharacterImage(userProgress.selectedCharacter)
        }

        // Update stats
        with(binding) {
            tvLives.text = userProgress.lives.toString()
            tvCoins.text = userProgress.totalCoins.toString()
        }

        updateStreakDisplay(userProgress)
    }

    private fun updateStreakDisplay(userProgress: UserProgress) {
        val (titleRes, subtitleRes) = when {
            userProgress.currentStreak == 0 ->
                R.string.streak_start to R.string.every_day_count
            else ->
                R.string.streak_count to R.string.keep_it_up
        }

        with(binding.layoutStreak) {
            tvStreakTitle.text = if (userProgress.currentStreak == 0) {
                getString(titleRes)
            } else {
                getString(titleRes, userProgress.currentStreak)
            }
            tvStreakSubtitle.text = getString(subtitleRes)
        }

        updateStreakFireIcons(userProgress.streakDays)
    }

    private fun updateStreakFireIcons(streakDays: String) {
        val activeDays = streakDays.split(",")
            .mapNotNull { it.toIntOrNull() }
            .toSet()

        val fireViews = with(binding.layoutStreak) {
            listOf(
                ivFireSunday, ivFireMonday, ivFireTuesday,
                ivFireWednesday, ivFireThursday, ivFireFriday, ivFireSaturday
            )
        }

        fireViews.forEachIndexed { index, imageView ->
            imageView.isVisible = index in activeDays
        }
    }

    override fun displayCategories(categories: List<Category>) {
        categoryAdapter.submitList(categories)
    }

    override fun displayLastGames(games: List<GameResult>) {
        lastGamesAdapter.submitList(games)

        val shouldShowSection = games.isNotEmpty()
        with(binding) {
            layoutLastGamesHeader.isVisible = shouldShowSection
            rvLastGames.isVisible = shouldShowSection
        }
    }

    private fun onCategoryClick(category: Category) {
        val action = HomeFragmentDirections.actionHomeToCharacterSelection(
            categoryId = category.id,
            categoryName = category.displayName
        )
        findNavController().navigate(action)
    }

    private fun onGameResultClick(gameResult: GameResult) {
        // TODO: Navigate to game result detail
    }

    private fun showSettingsDialog() {
        SettingsDialogFragment().show(childFragmentManager, SETTINGS_DIALOG_TAG)
    }

    private fun navigateToAllGames() {
        // TODO: Navigate to all games screen
        // findNavController().navigate(R.id.action_home_to_all_games)
    }

    private fun navigateToAllCategories() {
        // TODO: Navigate to all categories screen
        // findNavController().navigate(R.id.action_home_to_all_categories)
    }

    companion object {
        private const val LAST_GAMES_LIMIT = 3
        private const val SETTINGS_DIALOG_TAG = "settings_dialog"
    }
}