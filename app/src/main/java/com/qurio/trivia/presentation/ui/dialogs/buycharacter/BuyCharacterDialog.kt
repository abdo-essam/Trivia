package com.qurio.trivia.presentation.ui.dialogs.buycharacter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.databinding.DialogBuyCharacterBinding
import com.qurio.trivia.domain.model.Character
import com.qurio.trivia.presentation.base.BaseDialogFragment
import com.qurio.trivia.presentation.ui.dialogs.buycharacter.manager.BuyCharacterUIManager
import javax.inject.Inject

class BuyCharacterDialog : BaseDialogFragment(), BuyCharacterView {

    private var _binding: DialogBuyCharacterBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter: BuyCharacterPresenter

    private lateinit var uiManager: BuyCharacterUIManager
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as QuriοApp).appComponent.inject(this)

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
        initializeManagers()
        setupClickListeners()
        presenter.attachView(this)
        presenter.loadUserCoins()
    }

    private fun initializeManagers() {
        uiManager = BuyCharacterUIManager(binding)
        uiManager.displayCharacterData(character)
    }

    private fun setupClickListeners() {
        binding.apply {
            btnClose.setOnClickListener { dismiss() }
            btnCancel.setOnClickListener { dismiss() }
            btnBuy.setOnClickListener { presenter.purchaseCharacter(character) }
        }
    }

    override fun updateUserCoins(coins: Int) {
        uiManager.updateBuyButtonState(coins >= character.unlockCost)
    }

    override fun onPurchaseSuccess(character: Character, remainingCoins: Int) {
        showMessage(getString(R.string.character_purchased, character.displayName))
        onCharacterPurchasedListener?.invoke(character)
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
        presenter.loadUserCoins()
    }

    fun setOnCharacterPurchasedListener(listener: (Character) -> Unit) {
        onCharacterPurchasedListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }
}