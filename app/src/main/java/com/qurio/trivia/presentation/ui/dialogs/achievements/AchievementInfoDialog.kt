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
import com.qurio.trivia.presentation.base.BaseDialogFragment

class AchievementInfoDialog : BaseDialogFragment() {

    private var _binding: DialogAchievementDetailBinding? = null
    private val binding get() = _binding!!

    private var achievement: Achievement? = null

    companion object {
        const val TAG = "AchievementInfoDialog"

        private const val ARG_ID = "id"
        private const val ARG_NAME = "name"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_HOW_TO_GET = "how_to_get"
        private const val ARG_ICON_RES = "icon_res"
        private const val ARG_IS_UNLOCKED = "is_unlocked"
        private const val ARG_UNLOCKED_DATE = "unlocked_date"

        fun newInstance(achievement: Achievement): AchievementInfoDialog {
            return AchievementInfoDialog().apply {
                arguments = bundleOf(
                    ARG_ID to achievement.id,
                    ARG_NAME to achievement.name,
                    ARG_DESCRIPTION to achievement.description,
                    ARG_HOW_TO_GET to achievement.howToGet,
                    ARG_ICON_RES to achievement.iconRes,
                    ARG_IS_UNLOCKED to achievement.isUnlocked,
                    ARG_UNLOCKED_DATE to achievement.unlockedDate
                )
            }
        }

        // Legacy method for backward compatibility
        fun newInstance(
            name: String,
            description: String,
            howToGet: String,
            iconRes: Int,
            isUnlocked: Boolean
        ): AchievementInfoDialog {
            return AchievementInfoDialog().apply {
                arguments = bundleOf(
                    ARG_ID to "",
                    ARG_NAME to name,
                    ARG_DESCRIPTION to description,
                    ARG_HOW_TO_GET to howToGet,
                    ARG_ICON_RES to iconRes,
                    ARG_IS_UNLOCKED to isUnlocked,
                    ARG_UNLOCKED_DATE to null
                )
            }
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
        _binding = DialogAchievementDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        loadAchievementData()
        setupClickListeners()
        displayAchievementData()
    }

    // ========== Data Loading ==========

    private fun loadAchievementData() {
        achievement = extractArguments()
        if (achievement == null) {
            Log.e(TAG, "Missing achievement data")
            dismiss()
        }
    }

    private fun extractArguments(): Achievement? {
        return arguments?.let { args ->
            Achievement(
                id = args.getString(ARG_ID, ""),
                name = args.getString(ARG_NAME) ?: return null,
                description = args.getString(ARG_DESCRIPTION) ?: return null,
                howToGet = args.getString(ARG_HOW_TO_GET) ?: return null,
                iconRes = args.getInt(ARG_ICON_RES, -1).takeIf { it != -1 } ?: return null,
                isUnlocked = args.getBoolean(ARG_IS_UNLOCKED, false),
                unlockedDate = args.getLong(ARG_UNLOCKED_DATE, -1L).takeIf { it != -1L }
            )
        }
    }

    // ========== Setup Methods ==========

    private fun setupClickListeners() {
        binding.apply {
            btnClose.setOnClickListener {
                dismiss()
            }

            btnOk.setOnClickListener {
                dismiss()
            }

            btnShare.setOnClickListener {
                shareAchievement()
            }
        }
    }

    // ========== Display Data ==========

    private fun displayAchievementData() {
        val data = achievement ?: return

        with(binding) {
            // Title
            tvDialogTitle.text = data.name

            // Achievement card content
            tvAchievementName.text = data.name
            tvDescription.text = data.description
            tvHowToGet.text = data.howToGet
            ivBadge.setImageResource(data.iconRes)

            // Configure share button visibility
            configureShareButton(data.isUnlocked)

            Log.d(TAG, "Displaying achievement: ${data.name}, unlocked: ${data.isUnlocked}")
        }
    }

    // ========== UI Configuration ==========

    private fun configureShareButton(isUnlocked: Boolean) {
        binding.btnShare.isVisible = isUnlocked

        if (!isUnlocked) {
            expandOkButton()
            Log.d(TAG, "Achievement locked, hiding share button")
        } else {
            Log.d(TAG, "Achievement unlocked, showing share button")
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
        val data = achievement ?: return

        if (!data.isUnlocked) {
            Log.w(TAG, "Attempted to share locked achievement")
            showError(getString(R.string.unlock_achievement_first))
            return
        }

        val shareText = buildShareMessage(data)

        try {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.achievement_unlocked))
            }

            startActivity(
                Intent.createChooser(
                shareIntent,
                getString(R.string.share_achievement)
            ))

            Log.d(TAG, "Achievement shared: ${data.name}")
            dismiss()
        } catch (e: Exception) {
            Log.e(TAG, "Error sharing achievement", e)
            showError(getString(R.string.share_failed))
        }
    }

    private fun buildShareMessage(data: Achievement): String {
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
}