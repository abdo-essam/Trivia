package com.qurio.trivia.ui.home

import com.qurio.trivia.base.BaseView
import com.qurio.trivia.data.model.Category
import com.qurio.trivia.data.model.UserProgress

interface HomeView : BaseView {
    fun displayUserProgress(userProgress: UserProgress)
    fun displayCategories(categories: List<Category>)
}