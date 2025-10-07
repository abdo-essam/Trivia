package com.qurio.trivia.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.base.BaseFragment
import com.qurio.trivia.data.model.Category
import com.qurio.trivia.data.model.Difficulty
import com.qurio.trivia.databinding.FragmentGamesBinding
import com.qurio.trivia.databinding.TopBarBinding
import com.qurio.trivia.ui.adapters.AllGamesAdapter
import com.qurio.trivia.ui.dialogs.CharacterSelectionDialog
import com.qurio.trivia.ui.dialogs.DifficultyDialogFragment
import javax.inject.Inject

class GamesFragment : BaseFragment<FragmentGamesBinding, GamesPresenter>(), GamesView {

    @Inject
    override lateinit var presenter: GamesPresenter

    override val binding: FragmentGamesBinding by lazy {
        FragmentGamesBinding.inflate(layoutInflater)
    }

    private val topBarBinding: TopBarBinding by lazy {
        TopBarBinding.bind(binding.root.findViewById(R.id.top_bar))
    }

    private val allGamesAdapter by lazy {
        AllGamesAdapter(::onCategoryClick)
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
        setupRecyclerView()
        presenter.loadAllCategories()
    }

    private fun setupTopBar() {
        with(topBarBinding) {
            tvTitle.text = getString(R.string.games)
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun setupRecyclerView() {
        with(binding.rvAllGames) {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = allGamesAdapter
        }
    }

    override fun displayCategories(categories: List<Category>) {
        allGamesAdapter.submitList(categories)
    }

    private fun onCategoryClick(category: Category) {
        selectedCategory = category
        showCharacterSelectionDialog()
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
        val action = GamesFragmentDirections.actionGamesToGame(
            categoryId = categoryId,
            categoryName = categoryName,
            difficulty = difficulty.value
        )
        findNavController().navigate(action)
    }

    override fun showNotEnoughLives() {
        showError("Not enough lives! Please buy more lives from the home screen.")
    }
}