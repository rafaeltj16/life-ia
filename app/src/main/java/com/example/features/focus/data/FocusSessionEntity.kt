package com.example.features.focus.data

import java.util.UUID

data class FocusSessionEntity(
    val id: String = UUID.randomUUID().toString(),
    val projectName: String = "Projeto Landing Page",
    val taskName: String = "Revisar Copy",
    val durationMinutes: Int = 42,
    val elapsedSeconds: Int = 0,
    val interruptions: Int = 0,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false,
    val startTimeMillis: Long = System.currentTimeMillis(),
    val endTimeMillis: Long? = null,
    val focusScore: Int = 0,
    val earnedXp: Int = 0
)
