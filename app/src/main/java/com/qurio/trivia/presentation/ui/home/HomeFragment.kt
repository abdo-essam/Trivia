package com.qurio.trivia.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.databinding.FragmentHomeBinding
import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.domain.model.Difficulty
import com.qurio.trivia.domain.model.GameResult
import com.qurio.trivia.domain.model.UserProgress
import com.qurio.trivia.presentation.adapters.CategoryAdapter
import com.qurio.trivia.presentation.adapters.LastGamesAdapter
import com.qurio.trivia.presentation.base.BaseFragment
import com.qurio.trivia.presentation.ui.dialogs.achievements.AchievementsDialog
import com.qurio.trivia.presentation.ui.dialogs.buylife.BuyLifeDialog
import com.qurio.trivia.presentation.ui.dialogs.characterselection.CharacterSelectionDialog
import com.qurio.trivia.presentation.ui.dialogs.difficulty.DifficultyDialogFragment
import com.qurio.trivia.presentation.ui.dialogs.settings.SettingsDialogFragment
import javax.inject.Inject

/**
 * Home screen displaying user stats, categories, and recent game history
 */
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeView, HomePresenter>(), HomeView {

    @Inject
    lateinit var homePresenter: HomePresenter

    // Adapters
    private val categoryAdapter by lazy { CategoryAdapter(::onCategoryClick) }
    private val lastGamesAdapter by lazy { LastGamesAdapter() }

    // UI Updater
    private lateinit var uiUpdater: HomeUIUpdater

    // State
    private var selectedCategory: Category? = null

    // ========== Lifecycle ==========

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun initPresenter(): HomePresenter = homePresenter

    override fun setupViews() {
        uiUpdater = HomeUIUpdater(binding, requireContext())
        setupClickListeners()
        setupRecyclerViews()
        setupSectionHeaders()
        loadInitialData()
    }

    override fun onResume() {
        super.onResume()
        refreshDynamicData()
    }

    // ========== Setup ==========

    private fun setupClickListeners() {
        uiUpdater.setupClickListeners(
            onSettingsClick = ::showSettingsDialog,
            onCharacterClick = ::showCharacterSelectionDialog,
            onLivesClick = ::showBuyLifeDialog,
            onAwardsClick = ::showAchievementsDialog
        )
    }

    private fun setupRecyclerViews() {
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        binding.rvLastGames.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = lastGamesAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupSectionHeaders() {
        uiUpdater.setupSectionHeaders(
            gamesTitle = getString(R.string.games),
            lastGamesTitle = getString(R.string.last_games),
            onAllGamesClick = ::navigateToAllCategories,
            onAllLastGamesClick = ::navigateToAllLastGames
        )
    }

    private fun loadInitialData() {
        presenter.initializeData()
        presenter.loadCategories()
        refreshDynamicData()
    }

    private fun refreshDynamicData() {
        presenter.loadUserProgress()
        presenter.loadLastGames()
    }

    // ========== HomeView Implementation ==========

    override fun displayUserProgress(userProgress: UserProgress) {
        uiUpdater.updateUserProgress(userProgress)
    }

    override fun displayCategories(categories: List<Category>) {
        val hasCategories = categories.isNotEmpty()

        // Show/hide section and data
        binding.sectionHeaderGames.root.isVisible = hasCategories
        binding.rvCategories.isVisible = hasCategories

        // Update adapter
        categoryAdapter.submitList(categories)
    }

    override fun displayLastGames(games: List<GameResult>) {
        val hasGames = games.isNotEmpty()

        // Show/hide section and data
        binding.sectionHeaderLastGames.root.isVisible = hasGames
        binding.rvLastGames.isVisible = hasGames

        // Update adapter
        lastGamesAdapter.submitList(games)
    }

    override fun navigateToGame(categoryId: Int, categoryName: String, difficulty: Difficulty) {
        val action = HomeFragmentDirections.actionHomeToGame(
            categoryId = categoryId,
            categoryName = categoryName,
            difficulty = difficulty.value
        )
        findNavController().navigate(action)
    }

    override fun showNotEnoughLives() {
        showBuyLifeDialog()
    }

    // ========== User Interactions ==========

    private fun onCategoryClick(category: Category) {
        selectedCategory = category
        showDifficultyDialog()
    }

    // ========== Dialogs ==========

    private fun showCharacterSelectionDialog() {
        CharacterSelectionDialog.newInstance().apply {
            setOnCharacterSelectedListener {
                refreshDynamicData()
            }
        }.show(childFragmentManager, CharacterSelectionDialog.TAG)
    }

    private fun showDifficultyDialog() {
        DifficultyDialogFragment.newInstance().apply {
            setOnDifficultySelectedListener { difficulty ->
                presenter.checkLivesAndStartGame(selectedCategory, difficulty)
            }
        }.show(childFragmentManager, DifficultyDialogFragment.TAG)
    }

    private fun showBuyLifeDialog() {
        BuyLifeDialog.newInstance().apply {
            setOnLifePurchasedListener { _, _ ->
                refreshDynamicData()
            }
        }.show(childFragmentManager, BuyLifeDialog.TAG)
    }

    private fun showAchievementsDialog() {
        AchievementsDialog.newInstance()
            .show(childFragmentManager, AchievementsDialog.TAG)
    }

    private fun showSettingsDialog() {
        SettingsDialogFragment.newInstance()
            .show(childFragmentManager, SettingsDialogFragment.TAG)
    }

    // ========== Navigation ==========

    private fun navigateToAllCategories() {
        findNavController().navigate(R.id.action_home_to_games)
    }

    private fun navigateToAllLastGames() {
        findNavController().navigate(R.id.action_home_to_last_games)
    }
}