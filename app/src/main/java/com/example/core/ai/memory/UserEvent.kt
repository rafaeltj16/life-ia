package com.example.core.ai.memory

import java.time.Instant

sealed class UserEvent {

    data class TaskCompleted(
        val taskId: String,
        val completedAt: Instant
    ) : UserEvent()

    data class HabitCompleted(
        val habitId: String,
        val completedAt: Instant
    ) : UserEvent()

    data class ProjectUpdated(
        val projectId: String,
        val updatedAt: Instant
    ) : UserEvent()

    data class FocusSessionFinished(
        val minutes: Int,
        val finishedAt: Instant
    ) : UserEvent()
}
