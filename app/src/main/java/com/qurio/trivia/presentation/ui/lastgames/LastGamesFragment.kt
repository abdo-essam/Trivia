package com.qurio.trivia.presentation.ui.lastgames

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.presentation.base.BaseFragment
import com.qurio.trivia.data.model.GameResult
import com.qurio.trivia.databinding.FragmentLastGamesBinding
import com.qurio.trivia.databinding.TopBarBinding
import com.qurio.trivia.presentation.ui.adapters.LastGamesAdapter
import javax.inject.Inject

class LastGamesFragment : BaseFragment<FragmentLastGamesBinding, LastGamesView, LastGamesPresenter>(),
    LastGamesView {

    @Inject
    lateinit var lastGamesPresenter: LastGamesPresenter

    // ========== Lazy Bindings ==========

    private val topBarBinding: TopBarBinding by lazy {
        TopBarBinding.bind(binding.root.findViewById(R.id.top_bar))
    }

    private val lastGamesAdapter by lazy {
        LastGamesAdapter()
    }

    // ========== BaseFragment Implementation ==========

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

    // ========== Setup Methods ==========

    private fun setupTopBar() {
        with(topBarBinding) {
            tvTitle.text = getString(R.string.last_games)
            btnBack.setOnClickListener {
                navigateBack()
            }
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
        presenter.loadAllLastGames()
    }

    // ========== LastGamesView Implementation ==========

    override fun displayLastGames(games: List<GameResult>) {
        lastGamesAdapter.submitList(games)
        updateEmptyState(games.isEmpty())
    }

    // ========== UI Updates ==========

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.apply {
            layoutEmptyState.isVisible = isEmpty
            rvLastGames.isVisible = !isEmpty
        }
    }

    // ========== Navigation ==========

    private fun navigateBack() {
        findNavController().navigateUp()
    }
}