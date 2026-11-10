package com.example.features.auth.domain.repository

import com.example.core.network.SupabaseAuthResponse

interface AuthRepository {
    suspend fun login(email: String, password: String): SupabaseAuthResponse
    suspend fun signUp(name: String, email: String, password: String): SupabaseAuthResponse
    suspend fun recoverPassword(email: String)
}
