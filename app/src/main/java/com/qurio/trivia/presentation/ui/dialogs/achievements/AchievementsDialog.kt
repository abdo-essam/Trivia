package com.qurio.trivia.presentation.ui.dialogs.achievements

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.databinding.DialogAchievementsBinding
import com.qurio.trivia.domain.model.Achievement
import com.qurio.trivia.presentation.adapters.AchievementGridAdapter
import com.qurio.trivia.presentation.base.BaseDialogFragment
import com.qurio.trivia.presentation.ui.dialogs.achievement.AchievementInfoDialog
import javax.inject.Inject

/**
 * Dialog displaying all achievements with unlock status
 */
class AchievementsDialog : BaseDialogFragment(), AchievementsView {

    private var _binding: DialogAchievementsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter: AchievementsPresenter

    // Adapter
    private val achievementAdapter by lazy {
        AchievementGridAdapter(::onAchievementClick)
    }

    companion object {
        const val TAG = "AchievementsDialog"
        private const val GRID_SPAN_COUNT = 4

        fun newInstance(): AchievementsDialog {
            return AchievementsDialog()
        }
    }

    // ========== Lifecycle ==========

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as QuriοApp).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAchievementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        setupRecyclerView()
        setupClickListeners()
        loadAchievements()
    }

    // ========== Setup ==========

    private fun setupRecyclerView() {
        binding.rvAchievements.apply {
            layoutManager = GridLayoutManager(requireContext(), GRID_SPAN_COUNT)
            adapter = achievementAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnClose.setOnClickListener {
                Log.d(TAG, "Close button clicked")
                dismiss()
            }

            btnOk.setOnClickListener {
                Log.d(TAG, "OK button clicked")
                dismiss()
            }
        }
    }

    private fun loadAchievements() {
        presenter.attachView(this)
        presenter.loadAchievements()
    }

    // ========== AchievementsView Implementation ==========

    override fun displayAchievements(achievements: List<Achievement>) {
        Log.d(TAG, "Displaying ${achievements.size} achievements")

        val unlockedCount = achievements.count { it.isUnlocked }
        Log.d(TAG, "Unlocked: $unlockedCount/${achievements.size}")

        achievementAdapter.submitList(achievements)
    }

    // ========== User Interactions ==========

    private fun onAchievementClick(achievement: Achievement) {
        Log.d(TAG, "Achievement clicked: ${achievement.title}")
        showAchievementDetail(achievement)
    }

    private fun showAchievementDetail(achievement: Achievement) {
        AchievementInfoDialog.newInstance(achievement)
            .show(childFragmentManager, AchievementInfoDialog.TAG)
    }

    // ========== Lifecycle ==========

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }
}