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
import com.qurio.trivia.domain.repository.CharacterRepository
import com.qurio.trivia.presentation.adapters.CharacterGridAdapter
import com.qurio.trivia.presentation.base.BaseDialogFragment
import com.qurio.trivia.presentation.ui.dialogs.buycharacter.BuyCharacterDialog
import javax.inject.Inject

class CharacterSelectionDialog : BaseDialogFragment(), CharacterSelectionView {

    private var _binding: DialogCharacterSelectionBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter: CharacterSelectionPresenter

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
        private const val GRID_SPAN_COUNT = 5

        fun newInstance() = CharacterSelectionDialog()
    }

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

    private fun setupRecyclerView() {
        binding.rvCharacters.apply {
            layoutManager = GridLayoutManager(requireContext(), GRID_SPAN_COUNT)
            adapter = characterAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnClose.setOnClickListener { dismiss() }
            btnCancel.setOnClickListener { dismiss() }
            btnConfirm.setOnClickListener { confirmSelection() }
        }
    }

    private fun loadCharacters() {
        presenter.attachView(this)
        presenter.loadCharacters()
    }

    override fun displayCharacters(characters: List<CharacterRepository.CharacterWithStatus>) {
        Log.d(TAG, "Displaying ${characters.size} characters")
        characterAdapter.submitList(characters)

        // Auto-select currently selected character
        val selected = characters.firstOrNull { it.isSelected }?.character
        selected?.let { selectCharacter(it) }
    }

    override fun onCharacterSaved(character: Character) {
        Log.d(TAG, "Character saved: ${character.displayName}")
        onCharacterSelectedListener?.invoke(character)
        dismiss()
    }

    override fun onCharacterPurchased(character: Character) {
        Log.d(TAG, "Character purchased: ${character.displayName}")
        presenter.loadCharacters()
    }

    private fun onCharacterSelected(characterWithStatus: CharacterRepository.CharacterWithStatus) {
        if (characterWithStatus.isUnlocked) {
            selectCharacter(characterWithStatus.character)
        } else {
            onLockedCharacterClick(characterWithStatus.character)
        }
    }

    private fun selectCharacter(character: Character) {
        selectedCharacter = character
        updateConfirmButtonState(true)
        characterAdapter.setSelectedCharacter(character.characterName)
    }

    private fun onLockedCharacterClick(character: Character) {
        Log.d(TAG, "Locked character clicked: ${character.displayName}")
        showBuyCharacterDialog(character)
    }

    private fun confirmSelection() {
        val character = selectedCharacter

        if (character == null) {
            Log.w(TAG, "No character selected")
            showError("Please select a character")
            return
        }

        Log.d(TAG, "Confirming selection: ${character.displayName}")
        presenter.saveSelectedCharacter(character)
    }

    private fun updateConfirmButtonState(isEnabled: Boolean) {
        binding.btnConfirm.apply {
            this.isEnabled = isEnabled
            alpha = if (isEnabled) 1.0f else 0.5f
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

    private fun showBuyCharacterDialog(character: Character) {
        BuyCharacterDialog.newInstance(character).apply {
            setOnCharacterPurchasedListener { purchasedCharacter ->
                onCharacterPurchased(purchasedCharacter)
            }
        }.show(childFragmentManager, BuyCharacterDialog.TAG)
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