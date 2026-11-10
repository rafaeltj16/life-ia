package com.example.core.common

import java.util.UUID

data class GoalItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val targetValue: String,
    val progressValue: String,
    val progressPercent: Float,
    val isCompleted: Boolean = false,
    val userId: String? = null
)
