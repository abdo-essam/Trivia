package com.qurio.trivia.presentation.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.presentation.base.BaseFragment
import com.qurio.trivia.data.model.Category
import com.qurio.trivia.data.model.Difficulty
import com.qurio.trivia.databinding.FragmentGamesBinding
import com.qurio.trivia.databinding.TopBarBinding
import com.qurio.trivia.presentation.ui.adapters.AllGamesAdapter
import com.qurio.trivia.presentation.ui.dialogs.BuyLifeDialog
import com.qurio.trivia.presentation.ui.dialogs.CharacterSelectionDialog
import com.qurio.trivia.presentation.ui.dialogs.DifficultyDialogFragment
import javax.inject.Inject

class GamesFragment : BaseFragment<FragmentGamesBinding, GamesView, GamesPresenter>(), GamesView {

    @Inject
    lateinit var gamesPresenter: GamesPresenter

    // ========== Lazy Bindings ==========

    private val topBarBinding: TopBarBinding by lazy {
        TopBarBinding.bind(binding.root.findViewById(R.id.top_bar))
    }

    private val allGamesAdapter by lazy {
        AllGamesAdapter(::onCategoryClick)
    }

    // ========== State ==========

    private var selectedCategory: Category? = null

    // ========== BaseFragment Implementation ==========

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

    // ========== Setup Methods ==========

    private fun setupTopBar() {
        with(topBarBinding) {
            tvTitle.text = getString(R.string.games)
            btnBack.setOnClickListener {
                navigateBack()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvAllGames.apply {
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
        showCharacterSelectionDialog()
    }

    // ========== Navigation ==========

    private fun navigateBack() {
        findNavController().navigateUp()
    }

    // ========== Dialog Flow ==========

    private fun showCharacterSelectionDialog() {
        CharacterSelectionDialog().apply {
            setOnCharacterSelectedListener {
                showDifficultyDialog()
            }
        }.show(childFragmentManager, CharacterSelectionDialog.TAG)
    }

    private fun showDifficultyDialog() {
        DifficultyDialogFragment().apply {
            setOnDifficultySelectedListener { difficulty ->
                presenter.checkLivesAndStartGame(selectedCategory, difficulty)
            }
        }.show(childFragmentManager, DifficultyDialogFragment.TAG)
    }

    private fun showBuyLifeDialog() {
        BuyLifeDialog().apply {
            setOnPurchaseConfirmedListener {
                // User can buy lives and retry
                showError("Please purchase lives from the home screen")
                navigateBack()
            }
        }.show(childFragmentManager, BuyLifeDialog.TAG)
    }

    // ========== Constants ==========

    companion object {
        private const val GRID_SPAN_COUNT = 2
    }
}