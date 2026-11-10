package com.example.core.ai.model

data class DailyReview(
    val completedTasks: Int,
    val completedHabits: Int,
    val lifeScore: Int,
    val productivity: Int,
    val message: String
)
