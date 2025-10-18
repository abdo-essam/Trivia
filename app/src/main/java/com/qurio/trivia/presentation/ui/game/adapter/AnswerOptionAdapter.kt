package com.qurio.trivia.presentation.ui.game.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qurio.trivia.R
import com.qurio.trivia.databinding.ItemAnswerOptionBinding

class AnswerOptionAdapter(
    private val onAnswerClick: (Int) -> Unit
) : ListAdapter<AnswerOption, AnswerOptionAdapter.AnswerViewHolder>(AnswerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val binding = ItemAnswerOptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AnswerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class AnswerViewHolder(
        private val binding: ItemAnswerOptionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(answerOption: AnswerOption, position: Int) {
            binding.tvAnswerText.text = answerOption.text
            binding.tvAnswerText.setBackgroundResource(answerOption.state.backgroundRes)

            binding.root.apply {
                isEnabled = answerOption.isClickable
                setOnClickListener {
                    if (answerOption.isClickable) {
                        onAnswerClick(position)
                    }
                }
            }
        }
    }

    private class AnswerDiffCallback : DiffUtil.ItemCallback<AnswerOption>() {
        override fun areItemsTheSame(oldItem: AnswerOption, newItem: AnswerOption): Boolean {
            return oldItem.text == newItem.text
        }

        override fun areContentsTheSame(oldItem: AnswerOption, newItem: AnswerOption): Boolean {
            return oldItem == newItem
        }
    }
}

data class AnswerOption(
    val text: String,
    val state: AnswerState = AnswerState.DEFAULT,
    val isClickable: Boolean = true
)

enum class AnswerState(val backgroundRes: Int) {
    DEFAULT(R.drawable.bg_answer_option_default),
    SELECTED(R.drawable.bg_answer_option_selected),
    CORRECT(R.drawable.bg_answer_option_correct),
    INCORRECT(R.drawable.bg_answer_option_wrong)
}