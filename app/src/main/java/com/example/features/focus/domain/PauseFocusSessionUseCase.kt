package com.example.features.focus.domain

import com.example.features.focus.data.FocusRepository
import com.example.features.focus.data.FocusSessionEntity

class PauseFocusSessionUseCase(
    private val repository: FocusRepository
) {
    suspend operator fun invoke(currentSession: FocusSessionEntity): FocusSessionEntity {
        val updatedSession = currentSession.copy(isPaused = !currentSession.isPaused)
        repository.updateActiveSession(updatedSession)
        return updatedSession
    }
}
