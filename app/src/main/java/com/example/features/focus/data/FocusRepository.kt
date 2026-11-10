package com.example.features.focus.data

import kotlinx.coroutines.flow.Flow

interface FocusRepository {
    fun getActiveSession(): Flow<FocusSessionEntity?>
    fun getSessionHistory(): Flow<List<FocusSessionEntity>>
    suspend fun saveSession(session: FocusSessionEntity)
    suspend fun updateActiveSession(session: FocusSessionEntity)
    suspend fun clearActiveSession()
}
