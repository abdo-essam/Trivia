package com.qurio.trivia.presentation.base

interface BaseView {
    fun showLoading()
    fun hideLoading()
    fun showError(message: String)
    fun showNoConnection()
}