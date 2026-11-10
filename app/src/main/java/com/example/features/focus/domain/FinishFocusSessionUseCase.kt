package com.example.features.focus.domain

import com.example.features.focus.data.FocusRepository
import com.example.features.focus.data.FocusSessionEntity

class FinishFocusSessionUseCase(
    private val repository: FocusRepository,
    private val scoreCalculator: FocusScoreCalculator = FocusScoreCalculator()
) {
    suspend operator fun invoke(currentSession: FocusSessionEntity): FocusSessionEntity {
        val finalScore = scoreCalculator.calculateScore(
            durationMinutes = currentSession.durationMinutes,
            elapsedSeconds = currentSession.elapsedSeconds,
            interruptions = currentSession.interruptions
        )

        val finalXp = scoreCalculator.calculateEarnedXp(
            durationMinutes = currentSession.durationMinutes,
            elapsedSeconds = currentSession.elapsedSeconds,
            isFullyCompleted = true
        )

        val completedSession = currentSession.copy(
            isCompleted = true,
            isPaused = false,
            endTimeMillis = System.currentTimeMillis(),
            focusScore = finalScore,
            earnedXp = finalXp
        )

        repository.saveSession(completedSession)
        repository.clearActiveSession()
        return completedSession
    }
}
