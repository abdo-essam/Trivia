package com.qurio.trivia.presentation.ui.lastgames

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.databinding.FragmentLastGamesBinding
import com.qurio.trivia.databinding.TopBarBinding
import com.qurio.trivia.domain.model.GameResult
import com.qurio.trivia.presentation.adapters.LastGamesAdapter
import com.qurio.trivia.presentation.base.BaseFragment
import javax.inject.Inject

/**
 * Fragment displaying game history
 * Shows all previously played games with results
 */
class LastGamesFragment : BaseFragment<FragmentLastGamesBinding, LastGamesView, LastGamesPresenter>(),
    LastGamesView {

    @Inject
    lateinit var lastGamesPresenter: LastGamesPresenter

    private val topBarBinding: TopBarBinding by lazy {
        TopBarBinding.bind(binding.root.findViewById(R.id.top_bar))
    }

    private val lastGamesAdapter by lazy {
        LastGamesAdapter()
    }

    // ========== Lifecycle ==========

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLastGamesBinding {
        return FragmentLastGamesBinding.inflate(inflater, container, false)
    }

    override fun initPresenter(): LastGamesPresenter = lastGamesPresenter

    override fun setupViews() {
        setupTopBar()
        setupRecyclerView()
        loadData()
    }

    // ========== Setup ==========

    private fun setupTopBar() {
        topBarBinding.apply {
            tvTitle.text = getString(R.string.last_games)
            btnBack.setOnClickListener { navigateBack() }
        }
    }

    private fun setupRecyclerView() {
        binding.rvLastGames.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = lastGamesAdapter
            setHasFixedSize(true)
        }
    }

    private fun loadData() {
        presenter.loadAllGames()
    }

    // ========== LastGamesView Implementation ==========

    override fun displayLastGames(games: List<GameResult>) {
        lastGamesAdapter.submitList(games)
    }

    override fun showEmptyState() {
        binding.apply {
            layoutEmptyState.isVisible = true
            rvLastGames.isVisible = false
        }
    }

    override fun hideEmptyState() {
        binding.apply {
            layoutEmptyState.isVisible = false
            rvLastGames.isVisible = true
        }
    }

    // ========== Navigation ==========

    private fun navigateBack() {
        findNavController().navigateUp()
    }
}