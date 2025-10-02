package com.qurio.trivia.ui.achievements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.base.BaseFragment
import com.qurio.trivia.data.model.Achievement
import com.qurio.trivia.databinding.FragmentAchievementsBinding
import com.qurio.trivia.ui.adapters.AchievementAdapter
import javax.inject.Inject

class AchievementsFragment : BaseFragment<FragmentAchievementsBinding, AchievementsPresenter>(), AchievementsView {

    @Inject
    override lateinit var presenter: AchievementsPresenter

    override val binding: FragmentAchievementsBinding by lazy {
        FragmentAchievementsBinding.inflate(layoutInflater)
    }

    private lateinit var achievementAdapter: AchievementAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        return binding.root
    }

    override fun setupViews() {
        setupRecyclerView()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        presenter.loadAchievements()
    }

    override fun setupObservers() {
        // No observers needed
    }

    private fun setupRecyclerView() {
        achievementAdapter = AchievementAdapter()
        binding.rvAchievements.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = achievementAdapter
        }
    }

    override fun displayAchievements(achievements: List<Achievement>) {
        achievementAdapter.submitList(achievements)
    }
}