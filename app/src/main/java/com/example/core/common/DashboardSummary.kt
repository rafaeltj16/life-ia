package com.example.core.common

data class DashboardSummary(
    val userId: String,
    val tasksTodayCount: Int,
    val completedTasksTodayCount: Int,
    val activeHabitsCount: Int,
    val completedHabitsCount: Int,
    val habitStreak: Int,
    val level: Int,
    val xp: Int,
    val pendingProjectsCount: Int
)
