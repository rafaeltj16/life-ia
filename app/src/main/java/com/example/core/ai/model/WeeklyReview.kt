package com.example.core.ai.model

data class WeeklyReview(
    val completedTasks: Int,
    val completedHabits: Int,
    val averageLifeScore: Int,
    val strongestHabit: String,
    val weakestHabit: String,
    val recommendation: String
)
