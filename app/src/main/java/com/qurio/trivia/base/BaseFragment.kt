package com.qurio.trivia.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.airbnb.lottie.LottieAnimationView
import com.qurio.trivia.R
import com.qurio.trivia.ui.dialogs.NoConnectionDialogFragment

abstract class BaseFragment<VB : ViewBinding, P : BasePresenter<*>> : Fragment(), BaseView {

    protected abstract val binding: VB
    protected abstract val presenter: P

    private var loadingOverlay: View? = null
    private var lottieAnimation: LottieAnimationView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find loading overlay in the layout
        loadingOverlay = view.findViewById(R.id.loading_overlay)
        lottieAnimation = loadingOverlay?.findViewById(R.id.lottie_loading)

        setupViews()
        (presenter as? BasePresenter<BaseView>)?.attachView(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (presenter as? BasePresenter<BaseView>)?.detachView()
        hideLoading()
        loadingOverlay = null
        lottieAnimation = null
    }

    override fun showLoading() {
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
        val noConnectionDialog = NoConnectionDialogFragment()
        noConnectionDialog.show(childFragmentManager, "no_connection")
    }

    protected abstract fun setupViews()
}