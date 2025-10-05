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
import com.qurio.trivia.databinding.FragmentGamesBinding
import com.qurio.trivia.databinding.LayoutTopBarSimpleBinding
import com.qurio.trivia.ui.adapters.AllGamesAdapter
import javax.inject.Inject

class GamesFragment : BaseFragment<FragmentGamesBinding, GamesPresenter>(), GamesView {

    @Inject
    override lateinit var presenter: GamesPresenter

    override val binding: FragmentGamesBinding by lazy {
        FragmentGamesBinding.inflate(layoutInflater)
    }

    private val topBarBinding: LayoutTopBarSimpleBinding by lazy {
        LayoutTopBarSimpleBinding.bind(binding.root.findViewById(R.id.layout_top_bar))
    }

    private val allGamesAdapter by lazy {
        AllGamesAdapter(::onCategoryClick)
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
        val action = GamesFragmentDirections.actionGamesToCharacterSelection(
            categoryId = category.id,
            categoryName = category.displayName
        )
        findNavController().navigate(action)
    }
}