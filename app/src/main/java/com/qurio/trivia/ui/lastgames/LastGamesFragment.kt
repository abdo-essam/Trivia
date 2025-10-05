package com.qurio.trivia.ui.lastgames

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
import com.qurio.trivia.data.model.GameResult
import com.qurio.trivia.databinding.FragmentLastGamesBinding
import com.qurio.trivia.databinding.LayoutTopBarSimpleBinding
import com.qurio.trivia.ui.adapters.LastGamesAdapter
import javax.inject.Inject

class LastGamesFragment : BaseFragment<FragmentLastGamesBinding, LastGamesPresenter>(), LastGamesView {

    @Inject
    override lateinit var presenter: LastGamesPresenter

    override val binding: FragmentLastGamesBinding by lazy {
        FragmentLastGamesBinding.inflate(layoutInflater)
    }

    private val topBarBinding: LayoutTopBarSimpleBinding by lazy {
        LayoutTopBarSimpleBinding.bind(binding.root.findViewById(R.id.layout_top_bar))
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
        setupRecyclerView()
        presenter.loadAllLastGames()
    }

    override fun setupObservers() {
        // MVP pattern - no observers needed
    }

    private fun setupTopBar() {
        with(topBarBinding) {
            tvTitle.text = getString(R.string.last_games)
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun setupRecyclerView() {
        with(binding.rvLastGames) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = lastGamesAdapter
        }
    }

    override fun displayLastGames(games: List<GameResult>) {
        lastGamesAdapter.submitList(games)

        // Show empty state if no games
        binding.layoutEmptyState.isVisible = games.isEmpty()
        binding.rvLastGames.isVisible = games.isNotEmpty()
    }

    private fun onGameResultClick(gameResult: GameResult) {
        // TODO: Navigate to game result detail if needed
        // You can implement a detail screen to show full game statistics
    }
}