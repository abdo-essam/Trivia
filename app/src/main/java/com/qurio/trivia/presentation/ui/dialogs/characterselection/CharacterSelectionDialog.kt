package com.qurio.trivia.presentation.ui.dialogs.characterselection

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.databinding.DialogCharacterSelectionBinding
import com.qurio.trivia.domain.model.Character
import com.qurio.trivia.presentation.adapters.CharacterGridAdapter
import com.qurio.trivia.presentation.base.BaseDialogFragment
import com.qurio.trivia.presentation.ui.dialogs.buycharacter.BuyCharacterDialog
import javax.inject.Inject

/**
 * Dialog for selecting and purchasing characters
 * Displays all available characters in a grid layout
 */
class CharacterSelectionDialog : BaseDialogFragment(), CharacterSelectionView {

    private var _binding: DialogCharacterSelectionBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter: CharacterSelectionPresenter

    // State
    private var onCharacterSelectedListener: ((Character) -> Unit)? = null
    private var selectedCharacter: Character? = null

    // Adapter
    private val characterAdapter by lazy {
        CharacterGridAdapter(
            onCharacterSelected = ::onCharacterSelected,
            onLockedCharacterClick = ::onLockedCharacterClick
        )
    }

    companion object {
        const val TAG = "CharacterSelectionDialog"
        private const val GRID_SPAN_COUNT = 5
        private const val BUTTON_ENABLED_ALPHA = 1.0f
        private const val BUTTON_DISABLED_ALPHA = 0.5f

        fun newInstance(): CharacterSelectionDialog {
            return CharacterSelectionDialog()
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
        _binding = DialogCharacterSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        setupRecyclerView()
        setupClickListeners()
        loadCharacters()
    }

    // ========== Setup ==========

    private fun setupRecyclerView() {
        binding.rvCharacters.apply {
            layoutManager = GridLayoutManager(requireContext(), GRID_SPAN_COUNT)
            adapter = characterAdapter
            setHasFixedSize(true)
        }
    }

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

            btnConfirm.setOnClickListener {
                Log.d(TAG, "Confirm button clicked")
                confirmSelection()
            }
        }
    }

    private fun loadCharacters() {
        presenter.attachView(this)
        presenter.loadCharacters()
    }

    // ========== CharacterSelectionView Implementation ==========

    override fun displayCharacters(characters: List<Character>) {
        Log.d(TAG, "Displaying ${characters.size} characters")
        characterAdapter.submitList(characters)

        // Auto-select first unlocked or already selected character
        selectInitialCharacter(characters)
    }

    override fun onCharacterSaved(character: Character) {
        Log.d(TAG, "Character saved successfully: ${character.displayName}")

        //showMessage("Character ${character.displayName} selected!")
        onCharacterSelectedListener?.invoke(character)

        dismiss()
    }

    override fun onCharacterPurchased(characterName: String) {
        Log.d(TAG, "Character purchased: $characterName, reloading list")
        presenter.loadCharacters()
    }

    // ========== Character Selection ==========

    private fun selectInitialCharacter(characters: List<Character>) {
        val defaultCharacter = characters.firstOrNull { it.isSelected }
            ?: characters.firstOrNull { !it.isLocked }

        defaultCharacter?.let {
            selectCharacter(it)
        }
    }

    private fun onCharacterSelected(character: Character) {
        Log.d(TAG, "Character selected: ${character.displayName}")

        if (character.isLocked) {
            onLockedCharacterClick(character)
            return
        }

        selectCharacter(character)
    }

    private fun selectCharacter(character: Character) {
        selectedCharacter = character
        updateConfirmButtonState(true)
        characterAdapter.setSelectedCharacter(character.name)
    }

    private fun onLockedCharacterClick(character: Character) {
        Log.d(TAG, "Locked character clicked: ${character.displayName}")
        showBuyCharacterDialog(character)
    }

    private fun confirmSelection() {
        val character = selectedCharacter

        when {
            character == null -> {
                Log.w(TAG, "No character selected")
                showError("Please select a character")
            }
            character.isLocked -> {
                Log.w(TAG, "Attempted to select locked character: ${character.name}")
                showError("Please unlock this character first")
            }
            else -> {
                Log.d(TAG, "Saving character selection: ${character.displayName}")
                presenter.saveSelectedCharacter(character.name)
            }
        }
    }

    // ========== UI Updates ==========

    private fun updateConfirmButtonState(isEnabled: Boolean) {
        binding.btnConfirm.apply {
            this.isEnabled = isEnabled
            alpha = if (isEnabled) BUTTON_ENABLED_ALPHA else BUTTON_DISABLED_ALPHA
        }
    }

    override fun showLoading() {
        binding.apply {
            btnConfirm.isEnabled = false
            btnCancel.isEnabled = false
        }
    }

    override fun hideLoading() {
        binding.btnCancel.isEnabled = true
        updateConfirmButtonState(selectedCharacter != null)
    }

    // ========== Dialogs ==========

    private fun showBuyCharacterDialog(character: Character) {
        BuyCharacterDialog.newInstance(
            Character(
                name = character.name,
                displayName = character.displayName,
                age = character.age,
                description = character.description,
                imageRes = character.imageRes,
                lockedImageRes = character.lockedImageRes,
                unlockCost = character.unlockCost,
                isLocked = character.isLocked,
                isSelected = false
            )
        ).apply {
            setOnCharacterPurchasedListener { characterName ->
                //presenter.onCharacterPurchased(characterName)
            }
        }.show(childFragmentManager, BuyCharacterDialog.TAG)
    }

    // ========== Public API ==========

    /**
     * Set listener to be notified when character is selected
     */
    fun setOnCharacterSelectedListener(listener: (Character) -> Unit) {
        onCharacterSelectedListener = listener
    }

    // ========== Lifecycle ==========

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }
}