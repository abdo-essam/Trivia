package com.qurio.trivia.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.base.BaseFragment
import com.qurio.trivia.data.model.Category
import com.qurio.trivia.data.model.UserProgress
import com.qurio.trivia.databinding.FragmentHomeBinding
import com.qurio.trivia.ui.adapters.CategoryAdapter
import com.qurio.trivia.ui.dialogs.SettingsDialogFragment
import javax.inject.Inject

class HomeFragment : BaseFragment<FragmentHomeBinding, HomePresenter>(), HomeView {

    @Inject
    override lateinit var presenter: HomePresenter

    override val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity().application as QuriοApp).appComponent.inject(this)
        return binding.root
    }

    override fun setupViews() {
        setupRecyclerView()

        binding.btnSettings.setOnClickListener {
            showSettings()
        }

        binding.btnGames.setOnClickListener {
         // Navigate to category selection (which is already displayed)
        }
        presenter.loadUserProgress()
        presenter.loadCategories()
    }

    override fun setupObservers() {
        // No observers needed for MVP
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter { category ->
            navigateToCharacterSelection(category)
        }

        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }
    }

    private fun showSettings() {
        val settingsDialog = SettingsDialogFragment()
        settingsDialog.show(childFragmentManager, "settings")
    }

    private fun navigateToCharacterSelection(category: Category) {
        val action =
            HomeFragmentDirections.actionHomeToCharacterSelection(category.id, category.displayName)
        findNavController().navigate(action)
    }

    override fun displayUserProgress(userProgress: UserProgress) {
        binding.tvLives.text = userProgress.lives.toString()
        binding.tvCoins.text = userProgress.totalCoins.toString()
        binding.tvWelcomeMessage.text =
            "Welcome Curio explorer\n${getCharacterDisplayName(userProgress.selectedCharacter)}"

        // Load character image
        val characterImageRes = getCharacterImageRes(userProgress.selectedCharacter)
        binding.ivCharacter.setImageResource(characterImageRes)
    }

    override fun displayCategories(categories: List<Category>) {
        categoryAdapter.submitList(categories)
    }

    private fun getCharacterDisplayName(characterName: String): String {
        return characterName.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    private fun getCharacterImageRes(characterName: String): Int {
        // This would map to actual drawable resources
        return when (characterName) {
            "rika" -> R.drawable.character_rika
            "kaiyo" -> R.drawable.character_kaiyo
            "mimi" -> R.drawable.character_mimi
            "yoru" -> R.drawable.character_yoru
            "kuro" -> R.drawable.character_kuro
            "miko" -> R.drawable.character_miko
            "aori" -> R.drawable.character_aori
            "nara" -> R.drawable.character_nara
            "renji" -> R.drawable.character_renji
            else -> R.drawable.character_rika
        }
    }
}