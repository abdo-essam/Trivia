package com.qurio.trivia.presentation.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BasePresenter<V : BaseView> {

    protected var view: V? = null
        private set

    private var job: Job? = null
    private var presenterScope: CoroutineScope? = null

    /**
     * Attach view to presenter and create new coroutine scope
     */
    open fun attachView(view: V) {
        this.view = view
        // Create new job and scope each time view is attached
        job = SupervisorJob()
        presenterScope = CoroutineScope(Dispatchers.Main.immediate + job!!)
    }

    /**
     * Detach view and cancel all ongoing operations
     */
    open fun detachView() {
        view = null
        job?.cancel()
        job = null
        presenterScope = null
    }

    /**
     * Check if view is attached
     */
    protected fun isViewAttached(): Boolean = view != null

    /**
     * Safe view access - only executes if view is attached
     */
    protected inline fun withView(block: V.() -> Unit) {
        view?.block()
    }

    /**
     * Execute suspend function with automatic loading, error handling, and thread management
     *
     * @param execute The suspend function to execute (runs on background thread by default)
     * @param onSuccess Called on Main thread when execution succeeds
     * @param onError Called on Main thread when execution fails
     * @param showLoading Whether to show/hide loading indicator automatically
     * @param dispatcher The dispatcher to run the execute block on (default: IO)
     */
    protected fun <T> tryToExecute(
        execute: suspend () -> T,
        onSuccess: (T) -> Unit = {},
        onError: (Throwable) -> Unit = {},
        showLoading: Boolean = true,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): Job? {
        val scope = presenterScope ?: return null

        return scope.launch {
            try {
                if (showLoading) {
                    withView { showLoading() }
                }

                // Execute on background thread
                val result = withContext(dispatcher) {
                    execute()
                }

                // Success callback on Main thread
                if (showLoading) {
                    withView { hideLoading() }
                }
                onSuccess(result)

            } catch (e: Exception) {
                // Error handling on Main thread
                withView {
                    hideLoading()
                }
                onError(e)
            }
        }
    }

    /**
     * Execute suspend function without automatic error handling
     * Useful when you want manual control over loading/error states
     */
    protected fun launch(block: suspend CoroutineScope.() -> Unit): Job? {
        return presenterScope?.launch(block = block)
    }
}