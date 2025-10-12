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
        Log.d("BaseFragment", "onCreateView: ${this::class.java.simpleName}")
        _binding = initViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("BaseFragment", "onViewCreated: ${this::class.java.simpleName}")

        loadingOverlay = view.findViewById(R.id.loading_overlay)
        lottieAnimation = loadingOverlay?.findViewById(R.id.lottie_loading)

        @Suppress("UNCHECKED_CAST")
        presenter.attachView(this as V)

        setupViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("BaseFragment", "onDestroyView: ${this::class.java.simpleName}")

        presenter.detachView()
        hideLoading()
        loadingOverlay = null
        lottieAnimation = null
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
            showError("No internet connection")
        }
    }
}