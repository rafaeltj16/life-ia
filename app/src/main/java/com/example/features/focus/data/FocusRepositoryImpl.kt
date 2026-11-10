package com.example.features.focus.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FocusRepositoryImpl : FocusRepository {

    private val activeSessionState = MutableStateFlow<FocusSessionEntity?>(
        FocusSessionEntity(
            projectName = "Projeto Landing Page",
            taskName = "Revisar Copy",
            durationMinutes = 42,
            elapsedSeconds = 0,
            interruptions = 0
        )
    )

    private val sessionHistoryState = MutableStateFlow<List<FocusSessionEntity>>(emptyList())

    override fun getActiveSession(): Flow<FocusSessionEntity?> = activeSessionState.asStateFlow()

    override fun getSessionHistory(): Flow<List<FocusSessionEntity>> = sessionHistoryState.asStateFlow()

    override suspend fun saveSession(session: FocusSessionEntity) {
        val currentList = sessionHistoryState.value.toMutableList()
        currentList.add(0, session)
        sessionHistoryState.value = currentList
    }

    override suspend fun updateActiveSession(session: FocusSessionEntity) {
        activeSessionState.value = session
    }

    override suspend fun clearActiveSession() {
        activeSessionState.value = null
    }
}
