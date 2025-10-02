package com.qurio.trivia.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.qurio.trivia.ui.dialogs.LoadingDialogFragment
import com.qurio.trivia.ui.dialogs.NoConnectionDialogFragment

abstract class BaseFragment<VB : ViewBinding, P : BasePresenter<*>> : Fragment(), BaseView {

    protected abstract val binding: VB
    protected abstract val presenter: P

    private var loadingDialog: LoadingDialogFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
        (presenter as? BasePresenter<BaseView>)?.attachView(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (presenter as? BasePresenter<BaseView>)?.detachView()
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    override fun showLoading() {
        loadingDialog = LoadingDialogFragment()
        loadingDialog?.show(childFragmentManager, "loading")
    }

    override fun hideLoading() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun showNoConnection() {
        val noConnectionDialog = NoConnectionDialogFragment()
        noConnectionDialog.show(childFragmentManager, "no_connection")
    }

    protected abstract fun setupViews()
    protected abstract fun setupObservers()
}