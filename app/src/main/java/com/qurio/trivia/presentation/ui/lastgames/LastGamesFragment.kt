package com.qurio.trivia.presentation.ui.lastgames

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.databinding.FragmentLastGamesBinding
import com.qurio.trivia.domain.model.GameResult
import com.qurio.trivia.presentation.ui.lastgames.adapters.LastGamesAdapter
import com.qurio.trivia.presentation.base.BaseFragment
import com.qurio.trivia.presentation.ui.lastgames.managers.LastGamesUIManager
import javax.inject.Inject

class LastGamesFragment : BaseFragment<FragmentLastGamesBinding, LastGamesView, LastGamesPresenter>(),
    LastGamesView {

    @Inject
    lateinit var lastGamesPresenter: LastGamesPresenter

    private lateinit var uiManager: LastGamesUIManager

    private val lastGamesAdapter by lazy {
        LastGamesAdapter()
    }

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
        initializeManagers()
        setupTopBar()
        setupRecyclerView()
        loadData()
    }

    private fun initializeManagers() {
        uiManager = LastGamesUIManager(binding, lastGamesAdapter)
    }

    private fun setupTopBar() {
        uiManager.setupTopBar(
            title = getString(R.string.last_games),
            onBackClick = ::navigateBack
        )
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

    override fun displayLastGames(games: List<GameResult>) {
        uiManager.displayGames(games)
    }

    private fun navigateBack() {
        findNavController().navigateUp()
    }
}