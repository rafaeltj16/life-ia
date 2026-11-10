package com.example.features.auth.data.repository

import com.example.core.common.UserProfile
import com.example.features.auth.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImpl : UserRepository {
    private val localProfiles = mutableMapOf<String, UserProfile>()

    private fun getDefaultProfile(userId: String): UserProfile {
        return UserProfile(
            id = userId,
            email = "rafael@lifeos.ai",
            name = "Rafael",
            level = 10,
            xp = 1850,
            streak = 18
        )
    }

    override suspend fun getUserProfile(userId: String): UserProfile = withContext(Dispatchers.IO) {
        return@withContext localProfiles.getOrPut(userId) { getDefaultProfile(userId) }
    }

    override suspend fun saveUserProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        localProfiles[profile.id] = profile
    }
}
