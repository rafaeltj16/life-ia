package com.example.core.ai.model

data class DashboardData(
    val pendingTasks: Int,
    val overdueTasks: Int,
    val completedHabitsToday: Int,
    val totalHabitsToday: Int,
    val currentStreak: Int,
    val inactiveProjects: Int,
    val waterPercentage: Int
)
