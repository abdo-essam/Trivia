package com.qurio.trivia.presentation.ui.dialogs.achievements

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.data.model.Achievement
import com.qurio.trivia.databinding.DialogAchievementsBinding
import com.qurio.trivia.presentation.base.BaseDialogFragment
import com.qurio.trivia.presentation.adapters.AchievementGridAdapter
import javax.inject.Inject

class AchievementsDialog : BaseDialogFragment(), AchievementsView {

    private var _binding: DialogAchievementsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter: AchievementsPresenter

    private val achievementAdapter by lazy {
        AchievementGridAdapter(::onAchievementClick)
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

    // ========== Setup Methods ==========

    private fun setupRecyclerView() {
        binding.rvAchievements.apply {
            layoutManager = GridLayoutManager(requireContext(), GRID_SPAN_COUNT)
            adapter = achievementAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            Log.d(TAG, "Close button clicked")
            dismiss()
        }

        binding.btnOk.setOnClickListener {
            Log.d(TAG, "OK button clicked")
            dismiss()
        }
    }

    private fun loadAchievements() {
        presenter.attachView(this)
        presenter.loadAchievements()
    }

    // ========== AchievementsView Implementation ==========

    override fun displayAchievements(achievements: List<Achievement>) {
        Log.d(TAG, "Displaying ${achievements.size} achievements")
        achievementAdapter.submitList(achievements)
    }

    // ========== User Interactions ==========

    private fun onAchievementClick(achievement: Achievement) {
        Log.d(TAG, "Achievement clicked: ${achievement.title}")
        showAchievementDetail(achievement)
    }

    private fun showAchievementDetail(achievement: Achievement) {
        AchievementInfoDialog.Companion.newInstance(
            name = achievement.title,
            description = achievement.description,
            howToGet = achievement.howToGet,
            iconRes = if (achievement.isUnlocked) achievement.iconRes else achievement.iconLockedRes,
            isUnlocked = achievement.isUnlocked,
        ).show(childFragmentManager, AchievementInfoDialog.Companion.TAG)
    }

    // ========== Lifecycle ==========

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }

    companion object {
        const val TAG = "AchievementsDialog"
        private const val GRID_SPAN_COUNT = 4
    }
}