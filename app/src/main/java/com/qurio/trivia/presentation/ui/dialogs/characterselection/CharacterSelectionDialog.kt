package com.qurio.trivia.presentation.ui.dialogs.characterselection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.databinding.DialogCharacterSelectionBinding
import com.qurio.trivia.domain.model.Character
import com.qurio.trivia.domain.repository.CharacterRepository
import com.qurio.trivia.presentation.base.BaseDialogFragment
import com.qurio.trivia.presentation.ui.dialogs.buycharacter.BuyCharacterDialog
import com.qurio.trivia.presentation.ui.dialogs.characterinfo.CharacterInfoDialog
import com.qurio.trivia.presentation.ui.dialogs.characterselection.adapter.CharacterGridAdapter
import com.qurio.trivia.presentation.ui.dialogs.characterselection.manager.CharacterSelectionUIManager
import javax.inject.Inject

class CharacterSelectionDialog : BaseDialogFragment(), CharacterSelectionView {

    private var _binding: DialogCharacterSelectionBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter: CharacterSelectionPresenter

    private lateinit var uiManager: CharacterSelectionUIManager
    private var onCharacterSelectedListener: ((Character) -> Unit)? = null
    private var selectedCharacter: Character? = null

    private val characterAdapter by lazy {
        CharacterGridAdapter(
            onCharacterSelected = ::onCharacterSelected,
            onLockedCharacterClick = ::onLockedCharacterClick
        )
    }

    companion object {
        const val TAG = "CharacterSelectionDialog"
        fun newInstance() = CharacterSelectionDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        presenter.attachView(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCharacterSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        initializeManagers()
        setupClickListeners()
        presenter.loadCharacters()
    }

    private fun initializeManagers() {
        uiManager = CharacterSelectionUIManager(binding, characterAdapter)
        uiManager.setupRecyclerView()
    }

    private fun setupClickListeners() {
        binding.apply {
            btnClose.setOnClickListener { dismiss() }
            btnCancel.setOnClickListener { dismiss() }
            btnConfirm.setOnClickListener { confirmSelection() }
        }
    }

    override fun displayCharacters(characters: List<CharacterRepository.CharacterWithStatus>) {
        uiManager.displayCharacters(characters)
        characters.firstOrNull { it.isSelected }?.character?.let {
            selectCharacter(it)
        }
    }

    override fun onCharacterSaved(character: Character) {
        onCharacterSelectedListener?.invoke(character)
        dismiss()
    }

    override fun onCharacterPurchased(character: Character) {
        presenter.loadCharacters()
    }

    private fun onCharacterSelected(characterWithStatus: CharacterRepository.CharacterWithStatus) {
        if (characterWithStatus.isUnlocked) {
            showCharacterInfo(characterWithStatus.character)
        } else {
            onLockedCharacterClick(characterWithStatus.character)
        }
    }

    private fun selectCharacter(character: Character) {
        selectedCharacter = character
        uiManager.selectCharacter(character)
    }

    private fun showCharacterInfo(character: Character) {
        CharacterInfoDialog.newInstance(character).apply {
            setOnConfirmListener { selectCharacter(character) }
        }.show(childFragmentManager, CharacterInfoDialog.TAG)
    }

    private fun onLockedCharacterClick(character: Character) {
        showBuyCharacterDialog(character)
    }

    private fun showBuyCharacterDialog(character: Character) {
        BuyCharacterDialog.newInstance(character).apply {
            setOnCharacterPurchasedListener { purchasedCharacter ->
                onCharacterPurchased(purchasedCharacter)
            }
        }.show(childFragmentManager, BuyCharacterDialog.TAG)
    }

    private fun confirmSelection() {
        val character = selectedCharacter

        if (character == null) {
            showError("Please select a character")
            return
        }

        presenter.saveSelectedCharacter(character)
    }

    override fun showLoading() {
        uiManager.setLoadingState(true)
    }

    override fun hideLoading() {
        uiManager.setLoadingState(false)
    }

    fun setOnCharacterSelectedListener(listener: (Character) -> Unit) {
        onCharacterSelectedListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }
}