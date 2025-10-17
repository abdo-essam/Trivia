package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.Category

interface CategoryRepository {
    suspend fun getAllCategories(): List<Category>
    suspend fun getCategoryById(id: Int): Category?
    suspend fun getCategoryByName(name: String): Category?
}