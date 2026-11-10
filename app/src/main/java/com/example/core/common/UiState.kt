package com.example.core.common

data class UiState<T>(
    val loading: Boolean = false,
    val data: T? = null,
    val error: String? = null
)
