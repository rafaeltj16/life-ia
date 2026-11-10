package com.example.features.dashboard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dashboard_metrics")
data class DashboardMetricsEntity(
    @PrimaryKey val userId: String,
    val tasksToday: Int,
    val completedToday: Int,
    val habitStreak: Int,
    val weeklyGoal: Int,
    val pendingProjects: Int,
    val lastAiSummary: String,
    val updatedAt: String
)
