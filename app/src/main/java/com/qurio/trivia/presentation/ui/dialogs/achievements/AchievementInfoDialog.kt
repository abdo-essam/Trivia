package com.qurio.trivia.presentation.ui.dialogs.achievements

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.databinding.DialogAchievementDetailBinding
import com.qurio.trivia.domain.model.Achievement
import com.qurio.trivia.domain.model.AchievementState
import com.qurio.trivia.presentation.base.BaseDialogFragment
import com.qurio.trivia.presentation.mapper.getIcon

class AchievementInfoDialog : BaseDialogFragment() {

    private var _binding: DialogAchievementDetailBinding? = null
    private val binding get() = _binding!!

    private var achievementState: AchievementState? = null

    companion object {
        const val TAG = "AchievementInfoDialog"

        private const val ARG_ACHIEVEMENT_ID = "achievement_id"
        private const val ARG_TITLE = "title"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_HOW_TO_GET = "how_to_get"
        private const val ARG_IS_UNLOCKED = "is_unlocked"
        private const val ARG_PROGRESS = "progress"
        private const val ARG_MAX_PROGRESS = "max_progress"

        fun newInstance(state: AchievementState): AchievementInfoDialog {
            return AchievementInfoDialog().apply {
                arguments = bundleOf(
                    ARG_ACHIEVEMENT_ID to state.achievement.id,
                    ARG_TITLE to state.title,
                    ARG_DESCRIPTION to state.description,
                    ARG_HOW_TO_GET to state.howToGet,
                    ARG_IS_UNLOCKED to state.isUnlocked,
                    ARG_PROGRESS to state.progress,
                    ARG_MAX_PROGRESS to state.maxProgress
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as QuriοApp).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAchievementDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        loadAchievementData()
        setupClickListeners()
        displayAchievementData()
    }

    private fun loadAchievementData() {
        achievementState = extractAchievementFromArguments()
        if (achievementState == null) {
            Log.e(TAG, "Missing achievement data")
            dismiss()
        }
    }

    private fun extractAchievementFromArguments(): AchievementState? {
        return arguments?.let { args ->
            try {
                val achievementId = args.getString(ARG_ACHIEVEMENT_ID) ?: return null
                val achievement = Achievement.fromId(achievementId) ?: return null

                AchievementState(
                    achievement = achievement,
                    progress = args.getInt(ARG_PROGRESS, 0),
                    isUnlocked = args.getBoolean(ARG_IS_UNLOCKED, false)
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error extracting achievement", e)
                null
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnClose.setOnClickListener { dismiss() }
            btnOk.setOnClickListener { dismiss() }
            btnShare.setOnClickListener { shareAchievement() }
        }
    }

    private fun displayAchievementData() {
        val state = achievementState ?: return

        with(binding) {
            tvDialogTitle.text = state.title
            tvAchievementName.text = state.title
            tvDescription.text = state.description
            tvHowToGet.text = state.howToGet

            // Set badge icon
            val iconRes = state.achievement.getIcon(state.isUnlocked)
            ivBadge.setImageResource(iconRes)

            // Show decoration background if unlocked, locked background if locked
            ivBadgeDecoration.isVisible = state.isUnlocked
            ivLockedBackground.isVisible = !state.isUnlocked

            // Configure share button visibility
            configureShareButton(state.isUnlocked)

            Log.d(TAG, "Displaying: ${state.title}, unlocked: ${state.isUnlocked}, progress: ${state.progress}/${state.maxProgress}")
        }
    }

    private fun configureShareButton(isUnlocked: Boolean) {
        binding.btnShare.isVisible = isUnlocked

        if (!isUnlocked) {
            expandOkButton()
        }
    }

    private fun expandOkButton() {
        binding.btnOk.apply {
            val params = layoutParams as ConstraintLayout.LayoutParams
            params.apply {
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                marginStart = resources.getDimensionPixelSize(R.dimen.dialog_button_margin)
                marginEnd = resources.getDimensionPixelSize(R.dimen.dialog_button_margin)
            }
            layoutParams = params
        }
    }

    private fun shareAchievement() {
        val state = achievementState ?: return

        if (!state.isUnlocked) {
            showError(getString(R.string.unlock_achievement_first))
            return
        }

        val shareText = getString(
            R.string.share_achievement_message,
            state.title,
            state.description
        )

        try {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.achievement_unlocked))
            }

            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_achievement)))
            Log.d(TAG, "Achievement shared: ${state.title}")
            dismiss()
        } catch (e: Exception) {
            Log.e(TAG, "Error sharing achievement", e)
            showError(getString(R.string.share_failed))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}