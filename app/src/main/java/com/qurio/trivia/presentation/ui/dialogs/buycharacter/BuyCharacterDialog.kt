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

    private var onCharacterPurchasedListener: ((String) -> Unit)? = null

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
        val characterData = extractArguments() ?: run {
            Log.e(TAG, "Missing character data")
            dismiss()
            return
        }

        with(binding) {
            tvCost.text = characterData.cost.toString()
            ivCharacter.setImageResource(characterData.lockedImageRes)
        }
    }

    private fun extractArguments(): CharacterData? {
        return arguments?.let { args ->
            CharacterData(
                name = args.getString(ARG_CHARACTER_NAME) ?: return null,
                displayName = args.getString(ARG_CHARACTER_DISPLAY_NAME) ?: return null,
                cost = args.getInt(ARG_COST, -1).takeIf { it != -1 } ?: return null,
                lockedImageRes = args.getInt(ARG_IMAGE_RES, -1).takeIf { it != -1 } ?: return null
            )
        }
    }

    private fun loadUserCoins() {
        presenter.attachView(this)
        presenter.loadUserCoins()
    }

    // ========== Purchase Logic ==========

    private fun handlePurchase() {
        val characterData = extractArguments() ?: return
        presenter.purchaseCharacter(characterData.name, characterData.cost)
    }

    // ========== BuyCharacterView Implementation ==========

    override fun updateUserCoins(coins: Int) {

        val characterData = extractArguments() ?: return
        updateBuyButtonState(coins >= characterData.cost)
    }

    override fun onPurchaseSuccess(characterName: String, remainingCoins: Int) {
        Log.d(TAG, "Purchase successful: $characterName, remaining: $remainingCoins")

        showSuccessMessage(characterName)
        onCharacterPurchasedListener?.invoke(characterName)

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

    private fun showSuccessMessage(characterName: String) {
        showError(getString(R.string.character_purchased, characterName))
    }

    override fun showLoading() {
        binding.btnBuy.isEnabled = false
    }

    override fun hideLoading() {
        binding.btnBuy.isEnabled = true
    }

    // ========== Listener ==========

    fun setOnCharacterPurchasedListener(listener: (String) -> Unit) {
        onCharacterPurchasedListener = listener
    }

    // ========== Lifecycle ==========

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }

    // ========== Data Classes ==========

    private data class CharacterData(
        val name: String,
        val displayName: String,
        val cost: Int,
        val lockedImageRes: Int
    )

    // ========== Companion Object ==========

    companion object {
        const val TAG = "BuyCharacterDialog"

        private const val ARG_CHARACTER_NAME = "character_name"
        private const val ARG_CHARACTER_DISPLAY_NAME = "character_display_name"
        private const val ARG_COST = "cost"
        private const val ARG_IMAGE_RES = "image_res"

        fun newInstance(character: Character): BuyCharacterDialog {
            return BuyCharacterDialog().apply {
                arguments = bundleOf(
                    ARG_CHARACTER_NAME to character.name,
                    ARG_CHARACTER_DISPLAY_NAME to character.displayName,
                    ARG_COST to character.unlockCost,
                    ARG_IMAGE_RES to character.lockedImageRes
                )
            }
        }
    }
}