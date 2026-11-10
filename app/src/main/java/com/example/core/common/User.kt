package com.example.core.common

import com.squareup.moshi.Json
import java.time.LocalTime

// Auth States
sealed class AuthState {
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Authenticated(val email: String, val name: String) : AuthState()
}

// User Dashboard Metrics Model
data class UserDashboardMetrics(
    val userId: String,
    val tasksToday: Int,
    val completedToday: Int,
    val habitStreak: Int,
    val weeklyGoal: Int,
    val pendingProjects: Int,
    val lastAiSummary: String,
    val updatedAt: String
)

data class TimeRange(
    val start: LocalTime,
    val end: LocalTime
)

data class UserProfile(
    val id: String,
    val email: String,
    val name: String,
    val level: Int = 1,
    val xp: Int = 0,
    val streak: Int = 0,
    val wakeUpTime: LocalTime = LocalTime.of(7, 0),
    val sleepTime: LocalTime = LocalTime.of(23, 0),
    val bestFocusPeriod: TimeRange = TimeRange(LocalTime.of(9, 0), LocalTime.of(12, 0)),
    val worstFocusPeriod: TimeRange = TimeRange(LocalTime.of(14, 0), LocalTime.of(16, 0)),
    val strongestHabit: String = "",
    val weakestHabit: String = "",
    val productivityScore: Float = 0.0f,
    val currentGoal: String = ""
)

typealias User = UserProfile
