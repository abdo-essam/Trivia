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
import com.qurio.trivia.domain.model.UserAchievement
import com.qurio.trivia.presentation.base.BaseDialogFragment
import com.qurio.trivia.presentation.mapper.getIcon

class AchievementInfoDialog : BaseDialogFragment() {

    private var _binding: DialogAchievementDetailBinding? = null
    private val binding get() = _binding!!

    private var userAchievement: UserAchievement? = null

    companion object {
        const val TAG = "AchievementInfoDialog"

        private const val ARG_ACHIEVEMENT_ID = "achievement_id"
        private const val ARG_IS_UNLOCKED = "is_unlocked"
        private const val ARG_UNLOCKED_AT = "unlocked_at"
        private const val ARG_CURRENT_PROGRESS = "current_progress"

        fun newInstance(userAchievement: UserAchievement): AchievementInfoDialog {
            return AchievementInfoDialog().apply {
                arguments = bundleOf(
                    ARG_ACHIEVEMENT_ID to userAchievement.achievement.id,
                    ARG_IS_UNLOCKED to userAchievement.isUnlocked,
                    ARG_UNLOCKED_AT to userAchievement.unlockedAt,
                    ARG_CURRENT_PROGRESS to userAchievement.currentProgress
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
        userAchievement = extractAchievementFromArguments()
        if (userAchievement == null) {
            Log.e(TAG, "Missing achievement data")
            dismiss()
        }
    }

    private fun extractAchievementFromArguments(): UserAchievement? {
        return arguments?.let { args ->
            try {
                val achievementId = args.getString(ARG_ACHIEVEMENT_ID) ?: return null
                val achievement = Achievement.fromId(achievementId) ?: return null

                UserAchievement(
                    achievement = achievement,
                    isUnlocked = args.getBoolean(ARG_IS_UNLOCKED, false),
                    unlockedAt = args.getLong(ARG_UNLOCKED_AT, 0).takeIf { it > 0 },
                    currentProgress = args.getInt(ARG_CURRENT_PROGRESS, 0)
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
        val data = userAchievement ?: return

        with(binding) {
            tvDialogTitle.text = data.title
            tvAchievementName.text = data.title
            tvDescription.text = data.description
            tvHowToGet.text = data.howToGet

            // Set badge icon
            val iconRes = data.achievement.getIcon(data.isUnlocked)
            ivBadge.setImageResource(iconRes)

            // Show decoration background if unlocked, locked background if locked
            ivBadgeDecoration.isVisible = data.isUnlocked
            ivLockedBackground.isVisible = !data.isUnlocked

            // Configure share button visibility
            configureShareButton(data.isUnlocked)

            Log.d(TAG, "Displaying: ${data.title}, unlocked: ${data.isUnlocked}, progress: ${data.currentProgress}/${data.maxProgress}")
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
        val data = userAchievement ?: return

        if (!data.isUnlocked) {
            showError(getString(R.string.unlock_achievement_first))
            return
        }

        val shareText = getString(
            R.string.share_achievement_message,
            data.title,
            data.description
        )

        try {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.achievement_unlocked))
            }

            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_achievement)))
            Log.d(TAG, "Achievement shared: ${data.title}")
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