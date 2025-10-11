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

/**
 * Base class for all dialog fragments with common functionality
 */
abstract class BaseDialogFragment : DialogFragment(), BaseView {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDialog()
        setupViews()
    }

    /**
     * Setup dialog window properties
     */
    private fun setupDialog() {
        dialog?.window?.apply {
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // Add dim background
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setDimAmount(DIM_AMOUNT)

            // Add animations
            attributes?.windowAnimations = R.style.DialogAnimation
        }
    }

    /**
     * Setup views - to be implemented by subclasses
     */
    protected abstract fun setupViews()

    // ========== BaseView Implementation ==========

    override fun showLoading() {
        // Default implementation - can be overridden
    }

    override fun hideLoading() {
        // Default implementation - can be overridden
    }

    override fun showError(message: String) {
        if (isAdded) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showNoConnection() {
        if (isAdded) {
            showError("No internet connection")
        }
    }

    override fun getTheme(): Int = R.style.DialogTheme

    companion object {
        private const val DIM_AMOUNT = 0.7f
    }
}