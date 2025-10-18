package com.qurio.trivia.presentation.base

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import com.qurio.trivia.R
import com.qurio.trivia.utils.sound.playDialogCloseSound
import com.qurio.trivia.utils.sound.playDialogOpenSound

abstract class BaseDialogFragment : DialogFragment(), BaseView {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDialog()
        playDialogOpenSound()
        setupViews()
    }

    private fun setupDialog() {
        dialog?.window?.apply {
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setDimAmount(DIM_AMOUNT)

            attributes?.windowAnimations = R.style.DialogAnimation
        }
    }

    protected abstract fun setupViews()

    override fun showLoading() {}

    override fun hideLoading() {}

    override fun showError(message: String) {
        if (isAdded) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    fun showMessage(message: String) {
        if (isAdded) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showNoConnection() {
        if (isAdded) {
            showError("No internet connection")
        }
    }

    override fun dismiss() {
        playDialogCloseSound()
        super.dismiss()
    }

    override fun getTheme(): Int = R.style.DialogTheme

    companion object {
        private const val DIM_AMOUNT = 0.7f
    }
}