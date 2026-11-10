package com.lifeos.core_ai.learning

import java.time.Instant

sealed class LearningEvent {

    abstract val timestamp: Instant

    data class TaskCompleted(
        val taskId: String,
        val estimatedMinutes: Int,
        val actualMinutes: Int,
        override val timestamp: Instant
    ) : LearningEvent()

    data class HabitCompleted(
        val habitId: String,
        val hour: Int,
        override val timestamp: Instant
    ) : LearningEvent()

    data class NotificationOpened(
        val notificationId: String,
        val delaySeconds: Long,
        override val timestamp: Instant
    ) : LearningEvent()

    data class FocusSession(
        val durationMinutes: Int,
        val interruptions: Int,
        override val timestamp: Instant
    ) : LearningEvent()
}
