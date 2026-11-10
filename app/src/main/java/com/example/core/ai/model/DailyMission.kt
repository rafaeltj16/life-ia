package com.example.core.ai.model

data class DailyMission(
    val title: String,
    val description: String,
    val estimatedMinutes: Int,
    val priority: InsightPriority,
    val action: AIAction? = null
)
