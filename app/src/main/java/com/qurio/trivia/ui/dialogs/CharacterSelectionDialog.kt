package com.qurio.trivia.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.data.model.Character
import com.qurio.trivia.data.provider.DataProvider
import com.qurio.trivia.databinding.DialogCharacterSelectionBinding
import com.qurio.trivia.ui.adapters.CharacterGridAdapter

class CharacterSelectionDialog : BaseDialogFragment() {

    private var _binding: DialogCharacterSelectionBinding? = null
    private val binding get() = _binding!!

    private var onCharacterSelected: ((Character) -> Unit)? = null
    private var selectedCharacter: Character? = null

    private val characterAdapter by lazy {
        CharacterGridAdapter(
            onCharacterSelected = { character ->
                selectedCharacter = character
                binding.btnConfirm.isEnabled = true
            },
            onLockedCharacterClick = { character ->
                showBuyCharacterDialog(character)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        _binding = DialogCharacterSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        loadCharacters()
    }

    private fun setupViews() {
        // Setup RecyclerView
        binding.rvCharacters.apply {
            layoutManager = GridLayoutManager(requireContext(), 5)
            adapter = characterAdapter
            setHasFixedSize(true)
        }

        // Setup buttons
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnConfirm.setOnClickListener {
            selectedCharacter?.let { character ->
                onCharacterSelected?.invoke(character)
                dismiss()
            }
        }
    }

    private fun loadCharacters() {
        val characters = DataProvider.getCharacters()
        characterAdapter.submitList(characters)

        // Select first character by default
        selectedCharacter = characters.firstOrNull()
        binding.btnConfirm.isEnabled = true
    }

    private fun showBuyCharacterDialog(character: Character) {
        BuyCharacterDialog.newInstance(character).show(
            childFragmentManager,
            BuyCharacterDialog.TAG
        )
    }

    fun setOnCharacterSelectedListener(listener: (Character) -> Unit) {
        onCharacterSelected = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "CharacterSelectionDialog"
    }
}