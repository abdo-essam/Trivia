package com.qurio.trivia.presentation.ui.dialogs.achievements

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.qurio.trivia.databinding.DialogAchievementInfoBinding
import com.qurio.trivia.domain.model.UserAchievement
import com.qurio.trivia.presentation.base.BaseDialogFragment
import com.qurio.trivia.presentation.mapper.getIcon
import java.io.Serializable

class AchievementInfoDialog : BaseDialogFragment() {

    private var _binding: DialogAchievementInfoBinding? = null
    private val binding get() = _binding!!

    private var userAchievement: UserAchievement? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userAchievement = arguments?.getSerializable(ARG_ACHIEVEMENT) as? UserAchievement
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
            binding.apply {
                // Set title
                titleTxt.text = achievement.title

                // Set achievement name in card
                validationTxt.text = achievement.title

                // Set badge icon
                val iconRes = achievement.achievement.getIcon(achievement.isUnlocked)
                achievementImage.setImageResource(iconRes)

                // Set badge alpha based on unlock status
                achievementImage.alpha = if (achievement.isUnlocked) 1.0f else 0.5f

                // Show celebration background only when unlocked
                celebrateImage.isVisible = achievement.isUnlocked

                // Combine description and how to get
                val fullDescription = buildString {
                    append(achievement.description)
                    append("\n\n")
                    append(getString(com.qurio.trivia.R.string.how_to_get_it))
                    append("\n")
                    append(achievement.howToGet)
                }
                achievementDescription.text = fullDescription

                // Show share button only when unlocked
                btnShare.isVisible = achievement.isUnlocked

                // Adjust button constraints based on share button visibility
                updateButtonConstraints(achievement.isUnlocked)

                // Click listeners
                closeShape.setOnClickListener { dismiss() }
                btnOk.setOnClickListener { dismiss() }
                btnShare.setOnClickListener { shareAchievement(achievement) }
            }
        }
    }

    private fun updateButtonConstraints(isUnlocked: Boolean) {
        val okButtonParams = binding.btnOk.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams

        if (isUnlocked) {
            // Share button visible - OK button is connected to share button
            okButtonParams.startToEnd = binding.btnShare.id
            okButtonParams.marginStart = resources.getDimensionPixelSize(com.qurio.trivia.R.dimen.spacing_4)
            okButtonParams.endToEnd = binding.guideline2.id
            okButtonParams.matchConstraintPercentWidth = 0.48f
        } else {
            // Share button hidden - OK button takes full width
            okButtonParams.startToStart = binding.guideline.id
            okButtonParams.startToEnd = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET
            okButtonParams.endToEnd = binding.guideline2.id
            okButtonParams.marginStart = 0
            okButtonParams.matchConstraintPercentWidth = 1f
        }

        binding.btnOk.layoutParams = okButtonParams
    }

    private fun shareAchievement(achievement: UserAchievement) {
        val shareText = "🏆 I just unlocked the '${achievement.title}' achievement in Qurio Trivia!\n\n" +
                "${achievement.description}\n\n" +
                "Can you unlock it too? 🎮"

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        startActivity(Intent.createChooser(shareIntent, "Share Achievement"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}