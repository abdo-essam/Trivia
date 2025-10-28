package com.qurio.trivia.presentation.ui.home.managers

import com.qurio.trivia.databinding.SectionHeaderBinding

class SectionHeaderManager(
    private val binding: SectionHeaderBinding
) {
    fun setup(title: String, onAllClickListener: () -> Unit) {
        binding.apply {
            tvSectionTitle.text = title
            btnAll.setOnClickListener { onAllClickListener() }
        }
    }
}