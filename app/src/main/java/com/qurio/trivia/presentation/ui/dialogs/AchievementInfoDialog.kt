package com.qurio.trivia.presentation.ui.dialogs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.qurio.trivia.R
import com.qurio.trivia.databinding.DialogAchievementDetailBinding
import com.qurio.trivia.presentation.base.BaseDialogFragment

class AchievementInfoDialog : BaseDialogFragment() {

    private var _binding: DialogAchievementDetailBinding? = null
    private val binding get() = _binding!!

    // ========== Lifecycle ==========

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAchievementDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        setupClickListeners()
        displayAchievementData()
    }

    // ========== Setup Methods ==========

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

            btnShare.setOnClickListener {
                Log.d(TAG, "Share button clicked")
                shareAchievement()
            }
        }
    }

    // ========== Display Data ==========

    private fun displayAchievementData() {
        val achievementData = extractArguments() ?: run {
            Log.e(TAG, "Missing achievement data")
            dismiss()
            return
        }

        with(binding) {
            // Title
            tvDialogTitle.text = achievementData.name

            // Achievement card content
            tvAchievementName.text = achievementData.name
            tvDescription.text = achievementData.description
            tvHowToGet.text = achievementData.howToGet
            ivBadge.setImageResource(achievementData.iconRes)

            // Show/hide share button based on unlock status
            configureShareButton(achievementData.isUnlocked)
        }
    }

    private fun extractArguments(): AchievementData? {
        return arguments?.let { args ->
            AchievementData(
                name = args.getString(ARG_NAME) ?: return null,
                description = args.getString(ARG_DESCRIPTION) ?: return null,
                howToGet = args.getString(ARG_HOW_TO_GET) ?: return null,
                iconRes = args.getInt(ARG_ICON_RES, -1).takeIf { it != -1 } ?: return null,
                isUnlocked = args.getBoolean(ARG_IS_UNLOCKED, false)
            )
        }
    }

    // ========== UI Configuration ==========

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

    // ========== Share Functionality ==========

    private fun shareAchievement() {
        val achievementData = extractArguments() ?: return

        val shareText = buildShareMessage(achievementData)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        }

        startActivity(Intent.createChooser(
            shareIntent,
            getString(R.string.share_achievement)
        ))

        dismiss()
    }

    private fun buildShareMessage(data: AchievementData): String {
        return getString(
            R.string.share_achievement_message,
            data.name,
            data.description
        )
    }

    // ========== Lifecycle ==========

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ========== Data Classes ==========

    private data class AchievementData(
        val name: String,
        val description: String,
        val howToGet: String,
        val iconRes: Int,
        val isUnlocked: Boolean
    )

    // ========== Companion Object ==========

    companion object {
        const val TAG = "AchievementInfoDialog"

        private const val ARG_NAME = "name"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_HOW_TO_GET = "how_to_get"
        private const val ARG_ICON_RES = "icon_res"
        private const val ARG_IS_UNLOCKED = "is_unlocked"

        fun newInstance(
            name: String,
            description: String,
            howToGet: String,
            iconRes: Int,
            isUnlocked: Boolean
        ): AchievementInfoDialog {
            return AchievementInfoDialog().apply {
                arguments = bundleOf(
                    ARG_NAME to name,
                    ARG_DESCRIPTION to description,
                    ARG_HOW_TO_GET to howToGet,
                    ARG_ICON_RES to iconRes,
                    ARG_IS_UNLOCKED to isUnlocked
                )
            }
        }
    }
}