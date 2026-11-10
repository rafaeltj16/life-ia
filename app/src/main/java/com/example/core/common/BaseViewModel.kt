package com.example.core.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * BaseViewModel class containing reusable state and error handling mechanics.
 */
open class BaseViewModel : ViewModel() {

    protected val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    protected val _errorEvent = MutableSharedFlow<String>()
    val errorEvent: SharedFlow<String> = _errorEvent.asSharedFlow()

    protected val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    protected val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleException(throwable)
    }

    protected open fun handleException(throwable: Throwable) {
        _isLoading.value = false
        val message = throwable.localizedMessage ?: "Ocorreu um erro inesperado."
        _errorState.value = message
        _errorEvent.tryEmit(message)
    }

    fun clearError() {
        _errorState.value = null
    }

    /**
     * Executes a suspending block safely and returns a [Result] wrapper.
     */
    protected suspend fun <T> safeCall(block: suspend () -> T): Result<T> {
        return try {
            Result.Success(block())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Launches a coroutine in viewModelScope safely with loading state and global error handling.
     */
    protected fun <T> launchSafe(
        showLoading: Boolean = true,
        onError: ((Throwable) -> Unit)? = null,
        onSuccess: (suspend (T) -> Unit)? = null,
        block: suspend () -> T
    ) {
        if (showLoading) _isLoading.value = true
        _errorState.value = null
        
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                val data = block()
                _isLoading.value = false
                onSuccess?.invoke(data)
            } catch (throwable: Throwable) {
                _isLoading.value = false
                if (onError != null) {
                    onError(throwable)
                } else {
                    handleException(throwable)
                }
            }
        }
    }
}
