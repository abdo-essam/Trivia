package com.qurio.trivia.presentation.ui.dialogs.buycharacter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.databinding.DialogBuyCharacterBinding
import com.qurio.trivia.domain.model.Character
import com.qurio.trivia.presentation.base.BaseDialogFragment
import javax.inject.Inject

class BuyCharacterDialog : BaseDialogFragment(), BuyCharacterView {

    private var _binding: DialogBuyCharacterBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter: BuyCharacterPresenter

    private lateinit var character: Character
    private var onCharacterPurchasedListener: ((Character) -> Unit)? = null

    companion object {
        const val TAG = "BuyCharacterDialog"
        private const val ARG_CHARACTER_NAME = "character_name"

        fun newInstance(character: Character): BuyCharacterDialog {
            return BuyCharacterDialog().apply {
                arguments = bundleOf(ARG_CHARACTER_NAME to character.characterName)
            }
        }
    }

    // ========== Lifecycle ==========

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as QuriοApp).appComponent.inject(this)

        // Get character from enum
        val characterName = arguments?.getString(ARG_CHARACTER_NAME)
        character = Character.fromName(characterName ?: "") ?: Character.default()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogBuyCharacterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        setupClickListeners()
        displayCharacterData()
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

    // ========== Display Data ==========

    private fun displayCharacterData() {
        binding.apply {
            tvCost.text = character.unlockCost.toString()
            ivCharacter.setImageResource(character.lockedImageRes)
        }
    }

    private fun loadUserCoins() {
        presenter.attachView(this)
        presenter.loadUserCoins()
    }

    // ========== Purchase Logic ==========

    private fun handlePurchase() {
        presenter.purchaseCharacter(character)
    }

    // ========== BuyCharacterView Implementation ==========

    override fun updateUserCoins(coins: Int) {
        updateBuyButtonState(coins >= character.unlockCost)
    }

    override fun onPurchaseSuccess(character: Character, remainingCoins: Int) {
        Log.d(TAG, "Purchase successful: ${character.displayName}, remaining: $remainingCoins")
        showMessage(getString(R.string.character_purchased, character.displayName))
        onCharacterPurchasedListener?.invoke(character)
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

    override fun showLoading() {
        binding.apply {
            btnBuy.isEnabled = false
            btnCancel.isEnabled = false
        }
    }

    override fun hideLoading() {
        binding.btnCancel.isEnabled = true
        // Buy button state is controlled by coin balance
        presenter.loadUserCoins()
    }

    // ========== Listener ==========

    fun setOnCharacterPurchasedListener(listener: (Character) -> Unit) {
        onCharacterPurchasedListener = listener
    }

    // ========== Lifecycle ==========

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }
}