package com.example.features.focus.domain

import com.example.features.focus.data.FocusRepository
import com.example.features.focus.data.FocusSessionEntity

class StartFocusSessionUseCase(
    private val repository: FocusRepository
) {
    suspend operator fun invoke(
        projectName: String,
        taskName: String,
        durationMinutes: Int
    ): FocusSessionEntity {
        val newSession = FocusSessionEntity(
            projectName = projectName,
            taskName = taskName,
            durationMinutes = durationMinutes,
            elapsedSeconds = 0,
            interruptions = 0,
            isPaused = false,
            isCompleted = false,
            startTimeMillis = System.currentTimeMillis()
        )
        repository.updateActiveSession(newSession)
        return newSession
    }
}
