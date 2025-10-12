package com.qurio.trivia.presentation.ui.home.updaters

import com.qurio.trivia.databinding.SectionHeaderBinding

/**
 * Handles section header UI updates
 */
class SectionHeaderUpdater(
    private val binding: SectionHeaderBinding
) {
    fun setup(title: String, onAllClickListener: () -> Unit) {
        binding.apply {
            tvSectionTitle.text = title
            btnAll.setOnClickListener { onAllClickListener() }
        }
    }
}