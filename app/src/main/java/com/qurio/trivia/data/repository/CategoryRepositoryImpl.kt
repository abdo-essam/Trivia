package com.qurio.trivia.data.repository

import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.domain.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor() : CategoryRepository {

    override suspend fun getAllCategories(): List<Category> = withContext(Dispatchers.IO) {
        Category.all()
    }
}