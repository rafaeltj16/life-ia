package com.example.core.ai.model

import java.time.LocalTime

data class TimeBlock(
    val start: LocalTime,
    val end: LocalTime,
    val title: String,
    val description: String,
    val action: AIAction?,
    val category: BlockCategory
)

enum class BlockCategory {
    FOCUS,
    HABIT,
    MEETING,
    BREAK,
    HEALTH,
    PROJECT,
    TASK
}
