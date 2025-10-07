package com.qurio.trivia.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.qurio.trivia.R
import androidx.core.graphics.drawable.toDrawable

abstract class BaseDialogFragment : DialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDialog()
    }

    private fun setupDialog() {
        dialog?.window?.apply {
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // Add dim background
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setDimAmount(0.7f)

            // Add animations
            attributes?.windowAnimations = R.style.DialogAnimation
        }
    }

    override fun getTheme(): Int = R.style.DialogTheme
}