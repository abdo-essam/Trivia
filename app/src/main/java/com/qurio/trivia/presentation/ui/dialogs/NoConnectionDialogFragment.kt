package com.qurio.trivia.presentation.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.qurio.trivia.databinding.DialogNoConnectionBinding

class NoConnectionDialogFragment : DialogFragment() {

    private lateinit var binding: DialogNoConnectionBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNoConnectionBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        binding.btnRetry.setOnClickListener {
            dismiss()
            // Trigger retry in parent fragment
            (parentFragment as? RetryListener)?.onRetryClicked()
        }

        return dialog
    }

    interface RetryListener {
        fun onRetryClicked()
    }
}