package com.example.features.dashboard.data.remote

import com.squareup.moshi.Json

data class SupabaseDashboardMetricsDto(
    @Json(name = "user_id") val userId: String,
    @Json(name = "tasks_today") val tasksToday: Int,
    @Json(name = "completed_today") val completedToday: Int,
    @Json(name = "habit_streak") val habitStreak: Int,
    @Json(name = "weekly_goal") val weeklyGoal: Int,
    @Json(name = "pending_projects") val pendingProjects: Int,
    @Json(name = "last_ai_summary") val lastAiSummary: String,
    @Json(name = "updated_at") val updatedAt: String
)
