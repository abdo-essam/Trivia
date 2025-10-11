package com.qurio.trivia.presentation.ui.dialogs.characterselection

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.data.model.Character
import com.qurio.trivia.databinding.DialogCharacterSelectionBinding
import com.qurio.trivia.presentation.base.BaseDialogFragment
import com.qurio.trivia.presentation.adapters.CharacterGridAdapter
import com.qurio.trivia.presentation.ui.dialogs.buycharacter.BuyCharacterDialog
import javax.inject.Inject

class CharacterSelectionDialog : BaseDialogFragment(), CharacterSelectionView {

    private var _binding: DialogCharacterSelectionBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter: CharacterSelectionPresenter

    // ========== State ==========

    private var onCharacterSelectedListener: ((Character) -> Unit)? = null
    private var selectedCharacter: Character? = null
    private var allCharacters: List<Character> = emptyList()

    // ========== Adapter ==========

    private val characterAdapter by lazy {
        CharacterGridAdapter(
            onCharacterSelected = ::handleCharacterSelection,
            onLockedCharacterClick = ::handleLockedCharacterClick
        )
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

    // ========== Setup Methods ==========

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
        allCharacters = characters
        characterAdapter.submitList(characters)

        // Select first unlocked character by default
        selectDefaultCharacter(characters)
    }

    override fun onCharacterSaved() {
        Log.d(TAG, "Character saved successfully")
        selectedCharacter?.let { character ->
            onCharacterSelectedListener?.invoke(character)
        }
        dismiss()
    }

    // ========== Character Selection ==========

    private fun selectDefaultCharacter(characters: List<Character>) {
        val defaultCharacter = characters.firstOrNull { !it.isLocked }
        if (defaultCharacter != null) {
            selectedCharacter = defaultCharacter
            updateConfirmButton(true)
            characterAdapter.setSelectedCharacter(defaultCharacter.name)
        }
    }

    private fun handleCharacterSelection(character: Character) {
        Log.d(TAG, "Character selected: ${character.name}")

        if (character.isLocked) {
            handleLockedCharacterClick(character)
            return
        }

        selectedCharacter = character
        updateConfirmButton(true)
    }

    private fun handleLockedCharacterClick(character: Character) {
        Log.d(TAG, "Locked character clicked: ${character.name}")
        showBuyCharacterDialog(character)
    }

    private fun confirmSelection() {
        selectedCharacter?.let { character ->
            if (character.isLocked) {
                showError("Please select an unlocked character")
                return
            }

            // Save the selected character
            presenter.saveSelectedCharacter(character.name)
        } ?: showError("Please select a character")
    }

    // ========== UI Updates ==========

    private fun updateConfirmButton(isEnabled: Boolean) {
        binding.btnConfirm.apply {
            this.isEnabled = isEnabled
            alpha = if (isEnabled) 1.0f else 0.5f
        }
    }

    override fun showLoading() {
        binding.btnConfirm.isEnabled = false
    }

    override fun hideLoading() {
        updateConfirmButton(selectedCharacter != null)
    }

    // ========== Dialogs ==========

    private fun showBuyCharacterDialog(character: Character) {
        BuyCharacterDialog.newInstance(character).apply {
            setOnCharacterPurchasedListener { characterName ->
                Log.d(TAG, "Character purchased: $characterName")
                // Refresh character list
                //presenter.loadCharacters()

                // Automatically select the purchased character
                val purchasedCharacter = allCharacters.find { it.name == characterName }
                purchasedCharacter?.let {
                    handleCharacterSelection(it)
                    characterAdapter.setSelectedCharacter(it.name)
                }
            }
        }.show(childFragmentManager, BuyCharacterDialog.TAG)
    }

    // ========== Public API ==========

    fun setOnCharacterSelectedListener(listener: (Character) -> Unit) {
        onCharacterSelectedListener = listener
    }

    // ========== Lifecycle ==========

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }

    companion object {
        const val TAG = "CharacterSelectionDialog"
        private const val GRID_SPAN_COUNT = 5
    }
}