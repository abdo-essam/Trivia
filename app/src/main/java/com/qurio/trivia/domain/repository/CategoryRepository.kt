package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.Category

interface CategoryRepository {
    suspend fun getAllCategories(): List<Category>
}