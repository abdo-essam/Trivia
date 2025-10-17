package com.qurio.trivia.presentation.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.databinding.FragmentGamesBinding
import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.domain.model.Difficulty
import com.qurio.trivia.presentation.base.BaseFragment
import com.qurio.trivia.presentation.ui.dialogs.buylife.BuyLifeDialog
import com.qurio.trivia.presentation.ui.dialogs.difficulty.DifficultyDialogFragment
import com.qurio.trivia.presentation.ui.games.adapter.AllGamesAdapter
import com.qurio.trivia.presentation.ui.games.managers.GamesUIManager
import javax.inject.Inject

class GamesFragment : BaseFragment<FragmentGamesBinding, GamesView, GamesPresenter>(), GamesView {

    @Inject
    lateinit var gamesPresenter: GamesPresenter

    private lateinit var uiManager: GamesUIManager
    private var selectedCategory: Category? = null

    private val allGamesAdapter by lazy {
        AllGamesAdapter(::onCategoryClick)
    }

    companion object {
        private const val GRID_SPAN_COUNT = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGamesBinding {
        return FragmentGamesBinding.inflate(inflater, container, false)
    }

    override fun initPresenter(): GamesPresenter = gamesPresenter

    override fun setupViews() {
        initializeManagers()
        setupTopBar()
        setupRecyclerView()
        loadData()
    }

    private fun initializeManagers() {
        uiManager = GamesUIManager(binding, allGamesAdapter)
    }

    private fun setupTopBar() {
        uiManager.setupTopBar(
            title = getString(R.string.games),
            onBackClick = ::navigateBack
        )
    }

    private fun setupRecyclerView() {
        binding.rvGames.apply {
            layoutManager = GridLayoutManager(requireContext(), GRID_SPAN_COUNT)
            adapter = allGamesAdapter
            setHasFixedSize(true)
        }
    }

    private fun loadData() {
        presenter.loadAllCategories()
    }

    override fun displayCategories(categories: List<Category>) {
        uiManager.displayCategories(categories)
    }

    override fun navigateToGame(categoryId: Int, categoryName: String, difficulty: Difficulty) {
        val action = GamesFragmentDirections.actionGamesToGame(
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

    private fun navigateBack() {
        findNavController().navigateUp()
    }

    private fun showDifficultyDialog() {
        DifficultyDialogFragment.newInstance().apply {
            setOnDifficultySelectedListener { difficulty ->
                presenter.checkLivesAndStartGame(selectedCategory, difficulty)
            }
        }.show(childFragmentManager, DifficultyDialogFragment.TAG)
    }

    private fun showBuyLifeDialog() {
        BuyLifeDialog.newInstance().show(childFragmentManager, BuyLifeDialog.TAG)
    }
}