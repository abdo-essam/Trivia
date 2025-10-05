package com.qurio.trivia.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.qurio.trivia.R
import com.qurio.trivia.utils.NetworkUtils

abstract class BaseFragment<VB : ViewBinding, P : BasePresenter<*>> : Fragment(), BaseView {

    protected abstract val binding: VB
    protected abstract val presenter: P

    private var loadingOverlay: View? = null
    private var lottieAnimation: LottieAnimationView? = null
    private var noConnectionOverlay: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize overlays
        initializeOverlays(view)

        setupViews()
        (presenter as? BasePresenter<BaseView>)?.attachView(this)
    }

    private fun initializeOverlays(view: View) {
        // Loading overlay
        loadingOverlay = view.findViewById(R.id.loading_overlay)
        lottieAnimation = loadingOverlay?.findViewById(R.id.lottie_loading)

        // No connection overlay
        noConnectionOverlay = view.findViewById(R.id.no_connection_overlay)
        noConnectionOverlay?.findViewById<MaterialButton>(R.id.btn_retry)?.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                hideNoConnection()
                onRetryConnection()
            } else {
                showError(getString(R.string.still_no_connection))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (presenter as? BasePresenter<BaseView>)?.detachView()
        cleanupOverlays()
    }

    private fun cleanupOverlays() {
        hideLoading()
        loadingOverlay = null
        lottieAnimation = null
        noConnectionOverlay = null
    }

    override fun showLoading() {
        hideNoConnection()
        loadingOverlay?.isVisible = true
        lottieAnimation?.playAnimation()
    }

    override fun hideLoading() {
        loadingOverlay?.isVisible = false
        lottieAnimation?.cancelAnimation()
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun showNoConnection() {
        hideLoading()
        noConnectionOverlay?.isVisible = true
    }

    private fun hideNoConnection() {
        noConnectionOverlay?.isVisible = false
    }

    protected fun checkConnectionAndExecute(action: () -> Unit) {
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            action()
        } else {
            showNoConnection()
        }
    }

    protected open fun onRetryConnection() {
        // Override this in child fragments to implement retry logic
    }

    protected abstract fun setupViews()
}