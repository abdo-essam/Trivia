package com.qurio.trivia.presentation.ui.dialogs.buylife

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.databinding.DialogBuyLifeBinding
import com.qurio.trivia.presentation.base.BaseDialogFragment
import javax.inject.Inject

class BuyLifeDialog : BaseDialogFragment(), BuyLifeView {

    private var _binding: DialogBuyLifeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter: BuyLifePresenter

    private var onLifePurchasedListener: ((Int, Int) -> Unit)? = null

    companion object {
        const val TAG = "BuyLifeDialog"
        private const val LIFE_COST = 200

        fun newInstance(): BuyLifeDialog {
            return BuyLifeDialog()
        }
    }

    // ========== Lifecycle ==========

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as QuriοApp).appComponent.inject(this)
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
        setupClickListeners()
        displayLifeCost()
        loadUserCoins()
    }

    // ========== Setup Methods ==========

    private fun setupClickListeners() {
        binding.apply {
            btnClose.setOnClickListener {
                Log.d(TAG, "Close button clicked")
                dismiss()
            }

            btnCancel.setOnClickListener {
                Log.d(TAG, "Cancel button clicked")
                dismiss()
            }

            btnBuy.setOnClickListener {
                Log.d(TAG, "Buy button clicked")
                handlePurchase()
            }
        }
    }

    private fun displayLifeCost() {
        binding.tvCost.text = LIFE_COST.toString()
    }

    private fun loadUserCoins() {
        presenter.attachView(this)
        presenter.loadUserCoins()
    }

    // ========== Purchase Logic ==========

    private fun handlePurchase() {
        presenter.purchaseLife(LIFE_COST)
    }

    // ========== BuyLifeView Implementation ==========

    override fun updateUserCoins(coins: Int) {
        updateBuyButtonState(coins >= LIFE_COST)
    }

    override fun onPurchaseSuccess(remainingCoins: Int, remainingLives: Int) {
        Log.d(TAG, "Purchase successful: $remainingLives lives, $remainingCoins coins remaining")

        showSuccessMessage()
        onLifePurchasedListener?.invoke(remainingCoins, remainingLives)

        dismiss()
    }

    override fun showInsufficientCoins(currentCoins: Int, requiredCoins: Int) {
        val shortage = requiredCoins - currentCoins
        showError(getString(R.string.insufficient_coins_message, shortage))
    }

    // ========== UI Updates ==========

    private fun updateBuyButtonState(canAfford: Boolean) {
        binding.btnBuy.apply {
            isEnabled = canAfford
            alpha = if (canAfford) 1.0f else 0.5f
        }
    }

    private fun showSuccessMessage() {
       // showError(getString(R.string.life_purchased))
    }

    override fun showLoading() {
        binding.btnBuy.isEnabled = false
    }

    override fun hideLoading() {
        // Button state will be updated by updateUserCoins
    }

    // ========== Listener ==========

    fun setOnLifePurchasedListener(listener: (remainingCoins: Int, remainingLives: Int) -> Unit) {
        onLifePurchasedListener = listener
    }

    // ========== Lifecycle ==========

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }
}