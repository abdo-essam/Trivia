package com.qurio.trivia.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.qurio.trivia.R
import com.qurio.trivia.databinding.DialogLoadingBinding

class LoadingDialogFragment : DialogFragment() {

    private lateinit var binding: DialogLoadingBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogLoadingBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext(), R.style.LoadingDialogTheme)
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }
}