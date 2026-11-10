package com.example.features.goals.data

data class GoalEntity(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val targetProgress: Int,
    val currentProgress: Int,
    val targetDateMillis: Long,
    val priority: Int,
    val statusString: String
)
