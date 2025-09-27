package com.qurio.trivia.base

abstract class BasePresenter<V : BaseView> {
    protected var view: V? = null

    fun attachView(view: V) {
        this.view = view
    }

    fun detachView() {
        view = null
    }

    protected fun isViewAttached(): Boolean = view != null
}