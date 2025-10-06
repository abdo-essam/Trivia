package com.qurio.trivia.ui.dialogs

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.data.model.Achievement
import com.qurio.trivia.databinding.DialogAchievementsBinding
import com.qurio.trivia.ui.achievements.AchievementsPresenter
import com.qurio.trivia.ui.achievements.AchievementsView
import javax.inject.Inject

class AchievementsDialog : BaseDialogFragment(), AchievementsView {

    private var _binding: DialogAchievementsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter: AchievementsPresenter

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
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnOk.setOnClickListener { dismiss() }
    }

    private fun loadAchievements() {
        presenter.attachView(this)
        presenter.loadAchievements()
    }

    override fun displayAchievements(achievements: List<Achievement>) {
        binding.gridAchievements.removeAllViews()

        achievements.forEach { achievement ->
            val itemView = createAchievementItem(achievement)

            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setGravity(Gravity.CENTER)
            }

            binding.gridAchievements.addView(itemView, params)
        }
    }

    private fun createAchievementItem(achievement: Achievement): View {
        val itemView = layoutInflater.inflate(R.layout.item_achievement_grid, null)

        val badge = itemView.findViewById<ImageView>(R.id.iv_badge)
        val name = itemView.findViewById<TextView>(R.id.tv_achievement_title)

        // Set icon based on unlock status
        val iconRes = if (achievement.isUnlocked) {
            achievement.iconRes
        } else {
            achievement.iconLockedRes
        }
        badge.setImageResource(iconRes)

        // Set alpha for locked achievements
        badge.alpha = if (achievement.isUnlocked) 1.0f else 0.3f

        name.text = achievement.title
        name.setTextColor(
            if (achievement.isUnlocked) {
                requireContext().getColor(R.color.white)
            } else {
                requireContext().getColor(R.color.shade_tertiary)
            }
        )

        // Click listener
        itemView.setOnClickListener {
            showAchievementDetail(achievement)
        }

        return itemView
    }

    private fun showAchievementDetail(achievement: Achievement) {
        AchievementInfoDialog.newInstance(
            name = achievement.title,
            description = achievement.description,
            howToGet = achievement.howToGet,
            isUnlocked = achievement.isUnlocked,
            showShare = achievement.isUnlocked
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