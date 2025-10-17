package com.qurio.trivia.presentation.ui.dialogs.achievements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.databinding.DialogAchievementsBinding
import com.qurio.trivia.domain.model.UserAchievement
import com.qurio.trivia.presentation.ui.dialogs.achievements.adapter.AchievementGridAdapter
import com.qurio.trivia.presentation.base.BaseDialogFragment
import javax.inject.Inject

class AchievementsDialog : BaseDialogFragment(), AchievementsView {

    private var _binding: DialogAchievementsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter: AchievementsPresenter

    private val achievementAdapter by lazy {
        AchievementGridAdapter(::onAchievementClick)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        presenter.attachView(this)
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

    private fun setupRecyclerView() {
        binding.achievementsRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), GRID_SPAN_COUNT)
            adapter = achievementAdapter
            setHasFixedSize(true)
            post {
                requestLayout()
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            closeShape.setOnClickListener { dismiss() }
            btnOk.setOnClickListener { dismiss() }
        }
    }

    private fun loadAchievements() {
        presenter.loadAchievements()
    }

    override fun displayAchievements(achievements: List<UserAchievement>) {
        binding.achievementsRecyclerView.post {
            achievementAdapter.submitList(achievements)
        }
    }

    private fun onAchievementClick(userAchievement: UserAchievement) {
        AchievementInfoDialog.newInstance(userAchievement)
            .show(childFragmentManager, AchievementInfoDialog.TAG)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }

    companion object {
        const val TAG = "AchievementsDialog"
        private const val GRID_SPAN_COUNT = 4

        fun newInstance(): AchievementsDialog {
            return AchievementsDialog()
        }
    }

}