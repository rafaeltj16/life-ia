package com.example.features.auth.domain.repository

import com.example.core.common.UserProfile

interface UserRepository {
    suspend fun getUserProfile(userId: String): UserProfile
    suspend fun saveUserProfile(profile: UserProfile)
}
