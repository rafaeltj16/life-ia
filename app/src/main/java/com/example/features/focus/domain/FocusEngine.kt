package com.example.features.focus.domain

import com.example.features.focus.data.FocusSessionEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FocusEngine(
    private val scoreCalculator: FocusScoreCalculator = FocusScoreCalculator()
) {

    fun createTickerFlow(
        initialSession: FocusSessionEntity
    ): Flow<FocusSessionEntity> = flow {
        var currentSession = initialSession

        while (!currentSession.isCompleted) {
            delay(1000L)
            if (!currentSession.isPaused) {
                val newElapsed = currentSession.elapsedSeconds + 1
                val targetSeconds = currentSession.durationMinutes * 60
                val isCompleted = newElapsed >= targetSeconds

                val currentScore = scoreCalculator.calculateScore(
                    durationMinutes = currentSession.durationMinutes,
                    elapsedSeconds = newElapsed,
                    interruptions = currentSession.interruptions
                )

                val earnedXp = scoreCalculator.calculateEarnedXp(
                    durationMinutes = currentSession.durationMinutes,
                    elapsedSeconds = newElapsed,
                    isFullyCompleted = isCompleted
                )

                currentSession = currentSession.copy(
                    elapsedSeconds = newElapsed,
                    isCompleted = isCompleted,
                    focusScore = currentScore,
                    earnedXp = earnedXp
                )

                emit(currentSession)
            } else {
                emit(currentSession)
            }
        }
    }
}
