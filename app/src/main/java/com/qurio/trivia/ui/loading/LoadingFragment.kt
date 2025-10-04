package com.qurio.trivia.ui.loading

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.R
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.data.model.UserProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoadingFragment : Fragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var userProgressDao: UserProgressDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return View(requireContext()).apply {
            setBackgroundColor(resources.getColor(R.color.surface, null))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity().application as QuriοApp).appComponent.inject(this)

        checkFirstLaunchAndNavigate()
    }

    private fun checkFirstLaunchAndNavigate() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val isFirstLaunch = sharedPreferences.getBoolean("is_first_launch", true)

                if (isFirstLaunch) {
                    val initialProgress = UserProgress()
                    userProgressDao.insertOrUpdateUserProgress(initialProgress)
                }

                withContext(Dispatchers.Main) {
                    if (isFirstLaunch) {
                        findNavController().navigate(R.id.onboardingFragment)
                    } else {
                        findNavController().navigate(R.id.homeFragment)
                    }
                }
            }
        }
    }
}