package com.qurio.trivia.presentation.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.airbnb.lottie.LottieAnimationView
import com.qurio.trivia.R

/**
 * Base Fragment with ViewBinding and MVP support
 *
 * @param VB ViewBinding type
 * @param V View interface type (must extend BaseView)
 * @param P Presenter type
 */
abstract class BaseFragment<VB : ViewBinding, V : BaseView, P : BasePresenter<V>> :
    Fragment(), BaseView {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    // Use lazy initialization to ensure presenter is created after injection
    protected val presenter: P by lazy { initPresenter() }

    private var loadingOverlay: View? = null
    private var lottieAnimation: LottieAnimationView? = null

    // No Connection Overlay
    private var noConnectionOverlay: View? = null
    private var retryButton: Button? = null

    /**
     * Initialize ViewBinding
     */
    protected abstract fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VB

    /**
     * Initialize Presenter - called lazily after injection
     */
    protected abstract fun initPresenter(): P

    /**
     * Setup views after binding is initialized
     */
    protected abstract fun setupViews()

    /**
     * Handle retry action when no connection retry button is clicked
     * Override this in child fragments to implement retry logic
     */
    protected open fun onRetryConnection() {
        hideNoConnection()
        // Child fragments should override this to implement their retry logic
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("BaseFragment", "onCreateView: ${this::class.java.simpleName}")
        _binding = initViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("BaseFragment", "onViewCreated: ${this::class.java.simpleName}")

        // Initialize loading overlay
        loadingOverlay = view.findViewById(R.id.loading_overlay)
        lottieAnimation = loadingOverlay?.findViewById(R.id.lottie_loading)

        // Initialize no connection overlay
        noConnectionOverlay = view.findViewById(R.id.no_connection_overlay)
        retryButton = noConnectionOverlay?.findViewById(R.id.btn_retry)

        retryButton?.setOnClickListener {
            onRetryConnection()
        }

        @Suppress("UNCHECKED_CAST")
        presenter.attachView(this as V)

        setupViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("BaseFragment", "onDestroyView: ${this::class.java.simpleName}")

        presenter.detachView()
        hideLoading()
        hideNoConnection()

        loadingOverlay = null
        lottieAnimation = null
        noConnectionOverlay = null
        retryButton = null
        _binding = null
    }

    // ========== BaseView Implementation ==========

    override fun showLoading() {
        loadingOverlay?.isVisible = true
        lottieAnimation?.playAnimation()
    }

    override fun hideLoading() {
        loadingOverlay?.isVisible = false
        lottieAnimation?.cancelAnimation()
    }

    override fun showError(message: String) {
        if (isAdded) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showNoConnection() {
        if (isAdded && !childFragmentManager.isStateSaved) {
            hideLoading() // Hide loading if showing
            noConnectionOverlay?.isVisible = true
        }
    }

    // Add this method to hide no connection overlay
    fun hideNoConnection() {
        noConnectionOverlay?.isVisible = false
    }

    // Helper method to check if no connection overlay is showing
    fun isNoConnectionShowing(): Boolean {
        return noConnectionOverlay?.isVisible == true
    }
}