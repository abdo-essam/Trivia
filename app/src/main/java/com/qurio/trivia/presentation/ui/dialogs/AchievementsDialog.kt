package com.qurio.trivia.presentation.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.data.model.Achievement
import com.qurio.trivia.databinding.DialogAchievementsBinding
import com.qurio.trivia.presentation.ui.achievements.AchievementsPresenter
import com.qurio.trivia.presentation.ui.achievements.AchievementsView
import com.qurio.trivia.presentation.ui.adapters.AchievementGridAdapter
import javax.inject.Inject

class AchievementsDialog : BaseDialogFragment(), AchievementsView {

    private var _binding: DialogAchievementsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter: AchievementsPresenter

    private val achievementAdapter by lazy {
        AchievementGridAdapter { achievement ->
            showAchievementDetail(achievement)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        _binding = DialogAchievementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        loadAchievements()
    }

    private fun setupViews() {
        // Setup RecyclerView
        binding.rvAchievements.apply {
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = achievementAdapter
            setHasFixedSize(true)
        }

        // Button listeners
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnOk.setOnClickListener { dismiss() }
    }

    private fun loadAchievements() {
        presenter.attachView(this)
        presenter.loadAchievements()
    }

    override fun displayAchievements(achievements: List<Achievement>) {
        achievementAdapter.submitList(achievements)
    }

    private fun showAchievementDetail(achievement: Achievement) {
        AchievementInfoDialog.newInstance(
            name = achievement.title,
            description = achievement.description,
            howToGet = achievement.howToGet,
            iconRes = if (achievement.isUnlocked) achievement.iconRes else achievement.iconLockedRes,
            isUnlocked = achievement.isUnlocked
        ).show(childFragmentManager, AchievementInfoDialog.TAG)
    }

    override fun showLoading() {
        // Optional: Show loading state
    }

    override fun hideLoading() {
        // Optional: Hide loading state
    }

    override fun showError(message: String) {
        // Optional: Show error
    }

    override fun showNoConnection() {
        // Optional: Show no connection
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }

    companion object {
        const val TAG = "AchievementsDialog"
    }
}