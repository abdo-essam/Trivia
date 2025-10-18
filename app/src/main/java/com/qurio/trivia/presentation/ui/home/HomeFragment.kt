package com.qurio.trivia.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.databinding.FragmentHomeBinding
import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.domain.model.Difficulty
import com.qurio.trivia.domain.model.GameResult
import com.qurio.trivia.domain.model.UserProgress
import com.qurio.trivia.presentation.ui.lastgames.adapters.LastGamesAdapter
import com.qurio.trivia.presentation.base.BaseFragment
import com.qurio.trivia.presentation.ui.dialogs.achievements.AchievementsDialog
import com.qurio.trivia.presentation.ui.dialogs.buylife.BuyLifeDialog
import com.qurio.trivia.presentation.ui.dialogs.characterselection.CharacterSelectionDialog
import com.qurio.trivia.presentation.ui.dialogs.difficulty.DifficultyDialog
import com.qurio.trivia.presentation.ui.dialogs.settings.SettingsDialog
import com.qurio.trivia.presentation.ui.game.GameFragmentArgs
import com.qurio.trivia.presentation.ui.home.adapter.CategoryAdapter
import com.qurio.trivia.presentation.ui.home.carousel.CarouselConfigurator
import com.qurio.trivia.presentation.ui.home.managers.HomeUIManager
import javax.inject.Inject

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeView, HomePresenter>(), HomeView {

    @Inject
    lateinit var homePresenter: HomePresenter

    private val args: GameFragmentArgs by navArgs()

    private val categoryAdapter by lazy { CategoryAdapter(::onCategoryClick) }
    private val lastGamesAdapter by lazy { LastGamesAdapter() }
    private lateinit var uiManager: HomeUIManager
    private lateinit var carouselConfigurator: CarouselConfigurator
    private var selectedCategory: Category? = null

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
        initializeManagers()
        setupClickListeners()
        setupCategoryCarousel()
        setupRecyclerViews()
        setupSectionHeaders()
        loadInitialData()
    }

    override fun onResume() {
        super.onResume()
        presenter.checkAndUpdateStreak()
        refreshDynamicData()
    }

    private fun initializeManagers() {
        uiManager = HomeUIManager(binding, requireContext())
    }

    private fun setupClickListeners() {
        uiManager.setupClickListeners(
            onSettingsClick = ::showSettingsDialog,
            onCharacterClick = ::showCharacterSelectionDialog,
            onLivesClick = ::showBuyLifeDialog,
            onAwardsClick = ::showAchievementsDialog
        )
    }

    private fun setupCategoryCarousel() {
        binding.vpCategories.adapter = categoryAdapter

        carouselConfigurator = CarouselConfigurator(
            viewPager = binding.vpCategories,
            resources = resources,
            coroutineScope = lifecycleScope
        )

        lifecycle.addObserver(carouselConfigurator)
    }

    private fun setupRecyclerViews() {
        binding.rvLastGames.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = lastGamesAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupSectionHeaders() {
        uiManager.setupSectionHeaders(
            gamesTitle = getString(R.string.games),
            lastGamesTitle = getString(R.string.last_games),
            onAllGamesClick = ::navigateToAllCategories,
            onAllLastGamesClick = ::navigateToAllLastGames
        )
    }

    private fun loadInitialData() {
        // Check and update streak first when app opens
        presenter.checkAndUpdateStreak()
        presenter.loadCategories()
        refreshDynamicData()
    }

    private fun refreshDynamicData() {
        presenter.loadUserProgress()
        presenter.loadLastGames()
        presenter.loadUnlockedAchievements()
    }

    override fun displayUserProgress(userProgress: UserProgress) {
        uiManager.updateUserProgress(userProgress)
    }

    override fun displayUnlockedAchievements(unlockedCount: Int) {
        uiManager.updateUnlockedAchievements(unlockedCount)
    }

    override fun displayCategories(categories: List<Category>) {
        val hasCategories = categories.isNotEmpty()
        binding.sectionHeaderGames.root.isVisible = hasCategories
        binding.vpCategories.isVisible = hasCategories

        categoryAdapter.submitList(categories)

        if (hasCategories) {
            carouselConfigurator.start(categories.size)
        }
    }

    override fun displayLastGames(games: List<GameResult>) {
        val hasGames = games.isNotEmpty()
        binding.sectionHeaderLastGames.root.isVisible = hasGames
        binding.rvLastGames.isVisible = hasGames
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

    private fun onCategoryClick(category: Category) {
        selectedCategory = category
        showDifficultyDialog()
    }

    private fun showCharacterSelectionDialog() {
        CharacterSelectionDialog.newInstance().apply {
            setOnCharacterSelectedListener { refreshDynamicData() }
        }.show(childFragmentManager, CharacterSelectionDialog.TAG)
    }

    private fun showDifficultyDialog() {
        DifficultyDialog.newInstance().apply {
            setOnDifficultySelectedListener { difficulty ->
                presenter.checkLivesAndStartGame(selectedCategory, difficulty)
            }
        }.show(childFragmentManager, DifficultyDialog.TAG)
    }

    private fun showBuyLifeDialog() {
        BuyLifeDialog.newInstance().apply {
            setOnLifePurchasedListener { _, _ -> refreshDynamicData() }
        }.show(childFragmentManager, BuyLifeDialog.TAG)
    }

    private fun showAchievementsDialog() {
        AchievementsDialog.newInstance()
            .show(childFragmentManager, AchievementsDialog.TAG)
    }

    private fun showSettingsDialog() {
        SettingsDialog.newInstance()
            .show(childFragmentManager, SettingsDialog.TAG)
    }

    private fun navigateToAllCategories() {
        findNavController().navigate(R.id.action_home_to_games)
    }

    private fun navigateToAllLastGames() {
        findNavController().navigate(R.id.action_home_to_last_games)
    }

    override fun onDestroyView() {
        carouselConfigurator.stop()
        super.onDestroyView()
    }
}