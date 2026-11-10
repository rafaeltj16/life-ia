package com.example.features.auth.data.repository

import com.example.features.auth.domain.repository.AuthRepository
import com.example.core.network.SupabaseAuthResponse
import com.example.core.network.SupabaseServiceClient

class AuthRepositoryImpl : AuthRepository {
    override suspend fun login(email: String, password: String): SupabaseAuthResponse {
        return SupabaseServiceClient.authenticate(email, password)
    }

    override suspend fun signUp(name: String, email: String, password: String): SupabaseAuthResponse {
        return SupabaseServiceClient.register(name, email, password)
    }

    override suspend fun recoverPassword(email: String) {
        SupabaseServiceClient.recoverPassword(email)
    }
}
