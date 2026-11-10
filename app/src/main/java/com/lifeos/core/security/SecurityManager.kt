package com.lifeos.core.security

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}

class SecurityManager(
    private val tokenStore: TokenStore
) {
    private val _authState = MutableStateFlow<AuthState>(
        if (tokenStore.isUserLoggedIn()) AuthState.Authenticated else AuthState.Unauthenticated
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun authenticate(token: String) {
        tokenStore.saveToken(token)
        _authState.value = AuthState.Authenticated
    }

    fun logout() {
        tokenStore.clearToken()
        _authState.value = AuthState.Unauthenticated
    }

    fun isSessionValid(): Boolean {
        return tokenStore.isUserLoggedIn()
    }
}
