package com.qurio.trivia.ui.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.base.BaseFragment
import com.qurio.trivia.data.model.Character
import com.qurio.trivia.databinding.FragmentCharacterSelectionBinding
import com.qurio.trivia.ui.adapters.CharacterAdapter
import javax.inject.Inject

class CharacterSelectionFragment : BaseFragment<FragmentCharacterSelectionBinding, CharacterSelectionPresenter>(), CharacterSelectionView {

    @Inject
    override lateinit var presenter: CharacterSelectionPresenter

    override val binding: FragmentCharacterSelectionBinding by lazy {
        FragmentCharacterSelectionBinding.inflate(layoutInflater)
    }

    private val args: CharacterSelectionFragmentArgs by navArgs()
    private lateinit var characterAdapter: CharacterAdapter
    private var selectedCharacter: Character? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        return binding.root
    }

    override fun setupViews() {
        setupRecyclerView()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnContinue.setOnClickListener {
            selectedCharacter?.let { character ->
                presenter.selectCharacter(character)
            }
        }

        // Initially disable continue button
        binding.btnContinue.isEnabled = false

        presenter.loadCharacters()
    }

    private fun setupRecyclerView() {
        characterAdapter = CharacterAdapter { character, isSelected ->
            if (isSelected) {
                selectedCharacter = character
                binding.btnContinue.isEnabled = true
            }
        }

        binding.rvCharacters.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = characterAdapter
        }
    }

    override fun displayCharacters(characters: List<Character>) {
        characterAdapter.submitList(characters)
    }

    override fun navigateToDifficultySelection() {
        val action = CharacterSelectionFragmentDirections
            .actionCharacterSelectionToDifficulty(args.categoryId, args.categoryName)
        findNavController().navigate(action)
    }
}