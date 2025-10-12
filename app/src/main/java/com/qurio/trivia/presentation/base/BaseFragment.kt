package com.qurio.trivia.presentation.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("BaseFragment", "onCreateView: Initializing view binding ${this::class.java.simpleName} ")
        _binding = initViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("BaseFragment", "onViewCreated: View created, setting up presenter and views  ${this::class.java.simpleName}")
        // Find loading overlay in the layout (optional)
        loadingOverlay = view.findViewById(R.id.loading_overlay)
        lottieAnimation = loadingOverlay?.findViewById(R.id.lottie_loading)

        // Setup views first
        setupViews()

        // Attach presenter to view (lazy initialization ensures injection happened)
        @Suppress("UNCHECKED_CAST")
        presenter.attachView(this as V)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("BaseFragment", "onDestroyView: Cleaning up resources  ${this::class.java.simpleName}")
        // Detach presenter and cancel ongoing operations
        presenter.detachView()

        // Clean up loading views
        hideLoading()
        loadingOverlay = null
        lottieAnimation = null

        // Clean up binding
        _binding = null
    }

    // ========== BaseView Implementation ==========

    override fun showLoading() {
        Log.d("BaseFragment", "showLoading: Displaying loading overlay  ${this::class.java.simpleName}")
        loadingOverlay?.isVisible = true
        lottieAnimation?.playAnimation()
    }

    override fun hideLoading() {
        Log.d("BaseFragment", "hideLoading: Hiding loading overlay  ${this::class.java.simpleName}")
        loadingOverlay?.isVisible = false
        lottieAnimation?.cancelAnimation()
    }

    override fun showError(message: String) {
        Log.d("BaseFragment", "showError: Displaying error message: $message ${this::class.java.simpleName}")
        if (isAdded) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showNoConnection() {
        Log.d("BaseFragment", "showNoConnection: Displaying no connection message ${this::class.java.simpleName}")
        if (isAdded && !childFragmentManager.isStateSaved) {
            showError("No internet connection")
        }
    }
}