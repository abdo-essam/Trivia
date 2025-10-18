package com.qurio.trivia.presentation.ui.dialogs.buylife

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qurio.trivia.QurioApp
import com.qurio.trivia.R
import com.qurio.trivia.databinding.DialogBuyLifeBinding
import com.qurio.trivia.presentation.base.BaseDialogFragment
import com.qurio.trivia.presentation.ui.dialogs.buylife.manager.BuyLifeUIManager
import javax.inject.Inject

class BuyLifeDialog : BaseDialogFragment(), BuyLifeView {

    private var _binding: DialogBuyLifeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter: BuyLifePresenter

    private lateinit var uiManager: BuyLifeUIManager
    private var onLifePurchasedListener: ((Int, Int) -> Unit)? = null

    companion object {
        const val TAG = "BuyLifeDialog"
        private const val LIFE_COST = 200

        fun newInstance() = BuyLifeDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as QurioApp).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogBuyLifeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        initializeManagers()
        setupClickListeners()
        presenter.attachView(this)
        presenter.loadUserCoins()
    }

    private fun initializeManagers() {
        uiManager = BuyLifeUIManager(binding)
        uiManager.displayLifeCost(LIFE_COST)
    }

    private fun setupClickListeners() {
        binding.apply {
            btnClose.setOnClickListener { dismiss() }
            btnCancel.setOnClickListener { dismiss() }
            btnBuy.setOnClickListener { presenter.purchaseLife(LIFE_COST) }
        }
    }

    override fun updateUserCoins(coins: Int) {
        uiManager.updateBuyButtonState(coins >= LIFE_COST)
    }

    override fun onPurchaseSuccess(remainingCoins: Int, remainingLives: Int) {
        onLifePurchasedListener?.invoke(remainingCoins, remainingLives)
        dismiss()
    }

    override fun showInsufficientCoins(currentCoins: Int, requiredCoins: Int) {
        val shortage = requiredCoins - currentCoins
        showError(getString(R.string.insufficient_coins_message, shortage))
    }

    override fun showLoading() {
        uiManager.setLoadingState(true)
    }

    override fun hideLoading() {
        uiManager.setLoadingState(false)
    }

    fun setOnLifePurchasedListener(listener: (Int, Int) -> Unit) {
        onLifePurchasedListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }
}