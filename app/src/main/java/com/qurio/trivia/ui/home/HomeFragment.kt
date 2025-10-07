package com.qurio.trivia.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.base.BaseFragment
import com.qurio.trivia.data.model.Category
import com.qurio.trivia.data.model.Difficulty
import com.qurio.trivia.data.model.GameResult
import com.qurio.trivia.data.model.UserProgress
import com.qurio.trivia.databinding.FragmentHomeBinding
import com.qurio.trivia.databinding.ItemStatsBinding
import com.qurio.trivia.databinding.ItemStreakBinding
import com.qurio.trivia.databinding.SectionHeaderBinding
import com.qurio.trivia.databinding.TopBarHomeBinding
import com.qurio.trivia.ui.adapters.CategoryAdapter
import com.qurio.trivia.ui.adapters.LastGamesAdapter
import com.qurio.trivia.ui.dialogs.AchievementsDialog
import com.qurio.trivia.ui.dialogs.BuyLifeDialog
import com.qurio.trivia.ui.dialogs.CharacterSelectionDialog
import com.qurio.trivia.ui.dialogs.DifficultyDialogFragment
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

    private val topBarBinding: TopBarHomeBinding by lazy {
        TopBarHomeBinding.bind(binding.root.findViewById(R.id.top_bar_home))
    }

    private val statsBinding: ItemStatsBinding by lazy {
        ItemStatsBinding.bind(binding.root.findViewById(R.id.layout_stats))
    }

    private val streakBinding: ItemStreakBinding by lazy {
        ItemStreakBinding.bind(binding.root.findViewById(R.id.layout_streak))
    }

    private val sectionHeaderGamesBinding: SectionHeaderBinding by lazy {
        SectionHeaderBinding.bind(binding.root.findViewById(R.id.section_header_games))
    }

    private val sectionHeaderLastGamesBinding: SectionHeaderBinding by lazy {
        SectionHeaderBinding.bind(binding.root.findViewById(R.id.section_header_last_games))
    }

    private val categoryAdapter by lazy {
        CategoryAdapter(::onCategoryClick)
    }

    private val lastGamesAdapter by lazy {
        LastGamesAdapter(::onGameResultClick)
    }

    // Store selected category for dialog flow
    private var selectedCategory: Category? = null

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
        setupStatsSection()
        setupSectionHeaders()
        setupRecyclerViews()
        loadData()
    }

    private fun setupTopBar() {
        topBarBinding.btnSettings.setOnClickListener { showSettingsDialog() }
    }

    private fun setupStatsSection() {
        with(statsBinding) {
            // Lives click listener - Open Buy Life Dialog
            statsLivesContainer.setOnClickListener {
                showBuyLifeDialog()
            }

            // Awards click listener - Open Achievements Dialog
            statsAwardsContainer.setOnClickListener {
                showAchievementsDialog()
            }
        }
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
            }

            rvLastGames.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = lastGamesAdapter
                isNestedScrollingEnabled = false
            }
        }
    }

    private fun setupSectionHeaders() {
        with(sectionHeaderGamesBinding) {
            tvSectionTitle.text = getString(R.string.games)
            btnAll.setOnClickListener { navigateToAllCategories() }
        }
        with(sectionHeaderLastGamesBinding) {
            tvSectionTitle.text = getString(R.string.last_games)
            btnAll.setOnClickListener { navigateToAllGames() }
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
        with(statsBinding) {
            tvLives.text = userProgress.lives.toString()
            tvCoins.text = formatNumber(userProgress.totalCoins)
            tvAwards.text = userProgress.awards.toString()
        }

        updateStreakDisplay(userProgress)
    }

    private fun formatNumber(number: Int): String {
        return when {
            number >= 1000 -> String.format("%,d", number)
            else -> number.toString()
        }
    }

    private fun updateStreakDisplay(userProgress: UserProgress) {
        with(streakBinding) {
            if (userProgress.currentStreak == 0) {
                streakTitle.text = getString(R.string.streak_start)
                streakSubtitle.text = getString(R.string.every_day_count)
            } else {
                streakTitle.text = getString(R.string.streak_count, userProgress.currentStreak)
                streakSubtitle.text = getString(R.string.keep_it_up)
            }
        }

        updateStreakDays(userProgress.streakDays)
    }

    private fun updateStreakDays(streakDays: String) {
        val activeDays = streakDays.split(",")
            .mapNotNull { it.toIntOrNull() }
            .toSet()

        val dayViews = getDayViews()
        val dayLabels = listOf("S", "M", "T", "W", "Th", "F", "S")

        dayViews.forEachIndexed { index, (fireView, labelView) ->
            labelView.text = dayLabels[index]
            fireView.isVisible = index in activeDays
        }
    }

    private fun getDayViews(): List<Pair<ImageView, TextView>> {
        return with(streakBinding) {
            listOf(
                getDayViewPair(daySunday.root),
                getDayViewPair(dayMonday.root),
                getDayViewPair(dayTuesday.root),
                getDayViewPair(dayWednesday.root),
                getDayViewPair(dayThursday.root),
                getDayViewPair(dayFriday.root),
                getDayViewPair(daySaturday.root)
            )
        }
    }

    private fun getDayViewPair(dayView: View): Pair<ImageView, TextView> {
        val fireView = dayView.findViewById<ImageView>(R.id.ivDayFire)
        val labelView = dayView.findViewById<TextView>(R.id.tvDayLabel)
        return Pair(fireView, labelView)
    }

    override fun displayCategories(categories: List<Category>) {
        categoryAdapter.submitList(categories)
    }

    override fun displayLastGames(games: List<GameResult>) {
        lastGamesAdapter.submitList(games)
        val shouldShowSection = games.isNotEmpty()
        binding.rvLastGames.isVisible = shouldShowSection
    }

    private fun onCategoryClick(category: Category) {
        selectedCategory = category
        showCharacterSelectionDialog()
    }

    private fun onGameResultClick(gameResult: GameResult) {
        // TODO: Show game result detail dialog or replay
    }

    // Dialog flow: Category -> Character -> Difficulty -> Game
    private fun showCharacterSelectionDialog() {
        val dialog = CharacterSelectionDialog()
        dialog.setOnCharacterSelectedListener { selectedCharacter ->
            // Character selected, now show difficulty
            showDifficultyDialog()
        }
        dialog.show(childFragmentManager, CharacterSelectionDialog.TAG)
    }

    private fun showDifficultyDialog() {
        val dialog = DifficultyDialogFragment()
        dialog.setOnDifficultySelectedListener { difficulty ->
            // Difficulty selected, check lives and start game
            presenter.checkLivesAndStartGame(selectedCategory, difficulty)
        }
        dialog.show(childFragmentManager, DifficultyDialogFragment.TAG)
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

    private fun showBuyLifeDialog() {
        val dialog = BuyLifeDialog()
        dialog.setOnPurchaseConfirmedListener {
            presenter.purchaseLife()
        }
        dialog.show(childFragmentManager, BuyLifeDialog.TAG)
    }

    private fun showAchievementsDialog() {
        AchievementsDialog().show(childFragmentManager, AchievementsDialog.TAG)
    }

    private fun showSettingsDialog() {
        SettingsDialogFragment().show(childFragmentManager, SettingsDialogFragment.TAG)
    }

    private fun navigateToAllCategories() {
        findNavController().navigate(R.id.action_home_to_games)
    }

    private fun navigateToAllGames() {
        findNavController().navigate(R.id.action_home_to_last_games)
    }

    companion object {
        private const val LAST_GAMES_LIMIT = 3
    }
}