package com.qurio.trivia.ui.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.qurio.trivia.databinding.DialogAchievementInfoBinding

class AchievementInfoDialog : BaseDialogFragment() {

    private var _binding: DialogAchievementInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAchievementInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        loadAchievementData()
    }

    private fun setupViews() {
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnOk.setOnClickListener { dismiss() }
        binding.btnShare?.setOnClickListener { shareAchievement() }
    }

    private fun loadAchievementData() {
        val name = arguments?.getString(ARG_NAME) ?: return
        val description = arguments?.getString(ARG_DESCRIPTION) ?: return
        val howToGet = arguments?.getString(ARG_HOW_TO_GET) ?: return
        val isUnlocked = arguments?.getBoolean(ARG_IS_UNLOCKED, false) ?: false
        val showShare = arguments?.getBoolean(ARG_SHOW_SHARE, true) ?: true

        binding.apply {
            tvTitle.text = name
            tvAchievementName.text = name
            tvDescription.text = description
            tvHowToGet.text = howToGet

            // Show share button only for unlocked achievements
            btnShare?.isVisible = isUnlocked && showShare

            // TODO: Load achievement icon based on name and unlock status
            // ivBadge.setImageResource(getAchievementIcon(name, isUnlocked))
        }
    }

    private fun shareAchievement() {
        val name = arguments?.getString(ARG_NAME) ?: return
        val shareText = "I just unlocked the '$name' achievement in Qurio! 🎉"

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        startActivity(Intent.createChooser(shareIntent, "Share achievement"))
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
        private const val ARG_IS_UNLOCKED = "is_unlocked"
        private const val ARG_SHOW_SHARE = "show_share"

        fun newInstance(
            name: String,
            description: String,
            howToGet: String,
            isUnlocked: Boolean = false,
            showShare: Boolean = true
        ) = AchievementInfoDialog().apply {
            arguments = bundleOf(
                ARG_NAME to name,
                ARG_DESCRIPTION to description,
                ARG_HOW_TO_GET to howToGet,
                ARG_IS_UNLOCKED to isUnlocked,
                ARG_SHOW_SHARE to showShare
            )
        }
    }
}