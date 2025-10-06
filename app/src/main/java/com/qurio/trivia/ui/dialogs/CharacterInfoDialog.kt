package com.qurio.trivia.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.qurio.trivia.databinding.DialogCharacterInfoBinding
import com.qurio.trivia.utils.extensions.loadCharacterImage

class CharacterInfoDialog : DialogFragment() {

    private var _binding: DialogCharacterInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCharacterInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDialog()
        setupViews()
        loadCharacterData()
    }

    private fun setupDialog() {
        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun setupViews() {
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnOk.setOnClickListener { dismiss() }
    }

    private fun loadCharacterData() {
        val name = arguments?.getString(ARG_NAME) ?: return
        val age = arguments?.getString(ARG_AGE) ?: return
        val description = arguments?.getString(ARG_DESCRIPTION) ?: return

        binding.apply {
            tvCharacterName.text = name
            tvCharacterAge.text = age
            tvCharacterDescription.text = description
            ivCharacter.loadCharacterImage(name.lowercase())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "CharacterInfoDialog"
        private const val ARG_NAME = "name"
        private const val ARG_AGE = "age"
        private const val ARG_DESCRIPTION = "description"

        fun newInstance(name: String, age: String, description: String) =
            CharacterInfoDialog().apply {
                arguments = bundleOf(
                    ARG_NAME to name,
                    ARG_AGE to age,
                    ARG_DESCRIPTION to description
                )
            }
    }
}