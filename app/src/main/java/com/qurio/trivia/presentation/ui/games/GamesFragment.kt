package com.qurio.trivia.presentation.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.databinding.FragmentGamesBinding
import com.qurio.trivia.databinding.TopBarBinding
import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.domain.model.Difficulty
import com.qurio.trivia.presentation.ui.games.adapter.AllGamesAdapter
import com.qurio.trivia.presentation.base.BaseFragment
import com.qurio.trivia.presentation.ui.dialogs.buylife.BuyLifeDialog
import com.qurio.trivia.presentation.ui.dialogs.difficulty.DifficultyDialogFragment
import javax.inject.Inject

/**
 * Fragment displaying all available game categories
 * Users can select a category and difficulty to start a game
 */
class GamesFragment : BaseFragment<FragmentGamesBinding, GamesView, GamesPresenter>(), GamesView {

    @Inject
    lateinit var gamesPresenter: GamesPresenter

    private val topBarBinding: TopBarBinding by lazy {
        TopBarBinding.bind(binding.root.findViewById(R.id.top_bar))
    }

    private val allGamesAdapter by lazy {
        AllGamesAdapter(::onCategoryClick)
    }

    // Selected category state
    private var selectedCategory: Category? = null

    companion object {
        private const val GRID_SPAN_COUNT = 2
    }

    // ========== Lifecycle ==========

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
        setupTopBar()
        setupRecyclerView()
        loadData()
    }

    // ========== Setup ==========

    private fun setupTopBar() {
        topBarBinding.apply {
            tvTitle.text = getString(R.string.games)
            btnBack.setOnClickListener { navigateBack() }
        }
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

    // ========== GamesView Implementation ==========

    override fun displayCategories(categories: List<Category>) {
        allGamesAdapter.submitList(categories)
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

    // ========== User Interactions ==========

    private fun onCategoryClick(category: Category) {
        selectedCategory = category
        showDifficultyDialog()
    }

    // ========== Navigation ==========

    private fun navigateBack() {
        findNavController().navigateUp()
    }

    // ========== Dialogs ==========

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