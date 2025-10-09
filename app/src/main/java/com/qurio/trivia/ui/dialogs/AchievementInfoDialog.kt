package com.qurio.trivia.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.qurio.trivia.databinding.DialogAchievementDetailBinding

class AchievementInfoDialog : BaseDialogFragment() {

    private var _binding: DialogAchievementDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAchievementDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        loadAchievementData()
    }

    private fun setupViews() {
        binding.apply {
            btnClose.setOnClickListener { dismiss() }
            btnOk.setOnClickListener { dismiss() }
            btnShare.setOnClickListener {
                // TODO: Implement share functionality
                dismiss()
            }
        }
    }

    private fun loadAchievementData() {
        val name = arguments?.getString(ARG_NAME) ?: return
        val description = arguments?.getString(ARG_DESCRIPTION) ?: return
        val howToGet = arguments?.getString(ARG_HOW_TO_GET) ?: return
        val iconRes = arguments?.getInt(ARG_ICON_RES) ?: return
        val isUnlocked = arguments?.getBoolean(ARG_IS_UNLOCKED, false) ?: false

        binding.apply {
            tvDialogTitle.text = name
            tvAchievementName.text = name
            tvDescription.text = description
            tvHowToGet.text = howToGet
            ivBadge.setImageResource(iconRes)

            // Show share button only for unlocked achievements
            btnShare.isVisible = isUnlocked

            // Adjust button layout if share is hidden
            if (!isUnlocked) {
                // Make OK button take full width
                val params = btnOk.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
                params.startToStart = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
                params.endToEnd = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
                params.marginStart = resources.getDimensionPixelSize(com.qurio.trivia.R.dimen.text_body_medium)
                params.marginEnd = resources.getDimensionPixelSize(com.qurio.trivia.R.dimen.text_body_medium)
                btnOk.layoutParams = params
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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
        ) = AchievementInfoDialog().apply {
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