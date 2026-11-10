package com.lifeos.core.context

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface ContextRepository {
    fun getLatestSnapshot(): Flow<ContextSnapshot>
    suspend fun saveSnapshot(snapshot: ContextSnapshot)
    suspend fun getUserProfile(): UserProfile
}

class ContextRepositoryImpl : ContextRepository {
    private val _snapshotFlow = MutableStateFlow(
        ContextSnapshot(
            energy = 9,
            lifeScore = 95,
            mood = Mood.GREAT
        )
    )

    override fun getLatestSnapshot(): Flow<ContextSnapshot> = _snapshotFlow.asStateFlow()

    override suspend fun saveSnapshot(snapshot: ContextSnapshot) {
        _snapshotFlow.value = snapshot
    }

    override suspend fun getUserProfile(): UserProfile {
        return UserProfile()
    }
}
