package com.qurio.trivia.presentation.ui.dialogs.achievements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.qurio.trivia.R
import com.qurio.trivia.databinding.DialogAchievementInfoBinding
import com.qurio.trivia.domain.model.UserAchievement
import com.qurio.trivia.presentation.base.BaseDialogFragment
import com.qurio.trivia.presentation.mapper.getIcon
import com.qurio.trivia.presentation.ui.dialogs.achievements.manager.AchievementShareManager
import java.io.Serializable

class AchievementInfoDialog : BaseDialogFragment() {

    private var _binding: DialogAchievementInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var shareManager: AchievementShareManager
    private var userAchievement: UserAchievement? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userAchievement = arguments?.getSerializable(ARG_ACHIEVEMENT) as? UserAchievement
        shareManager = AchievementShareManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAchievementInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        userAchievement?.let { achievement ->
            displayAchievementInfo(achievement)
            setupClickListeners(achievement)
        }
    }

    private fun displayAchievementInfo(achievement: UserAchievement) {
        binding.apply {
            titleTxt.text = achievement.title
            validationTxt.text = achievement.title

            val iconRes = achievement.achievement.getIcon(achievement.isUnlocked)
            achievementImage.setImageResource(iconRes)
            achievementImage.alpha = if (achievement.isUnlocked) 1.0f else 0.5f

            celebrateImage.isVisible = achievement.isUnlocked

            achievementDescription.text = buildDescription(achievement)

            btnShare.isVisible = achievement.isUnlocked
            updateButtonLayout(achievement.isUnlocked)
        }
    }

    private fun buildDescription(achievement: UserAchievement): String {
        return buildString {
            append(achievement.description)
            append("\n\n")
            append(getString(R.string.how_to_get_it))
            append("\n")
            append(achievement.howToGet)
        }
    }

    private fun setupClickListeners(achievement: UserAchievement) {
        binding.apply {
            closeShape.setOnClickListener { dismiss() }
            btnOk.setOnClickListener { dismiss() }
            btnShare.setOnClickListener {
                shareManager.shareAchievement(achievement)
            }
        }
    }

    private fun updateButtonLayout(isUnlocked: Boolean) {
        val okButtonParams = binding.btnOk.layoutParams as
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams

        if (isUnlocked) {
            okButtonParams.startToEnd = binding.btnShare.id
            okButtonParams.marginStart =
                resources.getDimensionPixelSize(R.dimen.spacing_4)
            okButtonParams.matchConstraintPercentWidth = 0.48f
        } else {
            okButtonParams.startToStart = binding.guideline.id
            okButtonParams.startToEnd =
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET
            okButtonParams.marginStart = 0
            okButtonParams.matchConstraintPercentWidth = 1f
        }

        binding.btnOk.layoutParams = okButtonParams
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AchievementInfoDialog"
        private const val ARG_ACHIEVEMENT = "arg_achievement"

        fun newInstance(userAchievement: UserAchievement): AchievementInfoDialog {
            return AchievementInfoDialog().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ACHIEVEMENT, userAchievement as Serializable)
                }
            }
        }
    }

}