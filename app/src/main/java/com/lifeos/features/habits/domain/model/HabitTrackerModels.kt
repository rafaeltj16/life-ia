package com.lifeos.features.habits.domain.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Data structure representing parameters to define a new daily habit.
 */
data class HabitDefinition(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val category: String = "HEALTH",
    val iconName: String = "⚡",
    val colorHex: String = "#3B82F6",
    val frequency: String = "DAILY",
    val targetDays: Int = 7,
    val targetValue: Int = 1,
    val unit: String = "vez",
    val reminderTime: String? = null
)

/**
 * Weekly status item for visual habit tracking.
 */
data class DayCompletionStatus(
    val date: String,
    val dayName: String, // e.g. "Seg", "Ter", "Qua"
    val dayNumber: Int,
    val isCompleted: Boolean,
    val isToday: Boolean
)

/**
 * High-level UI presentation model for a habit in the tracker.
 */
data class HabitUiItem(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val icon: String,
    val colorHex: String,
    val streak: Int,
    val bestStreak: Int,
    val targetDaysPerWeek: Int,
    val targetValue: Int,
    val unit: String,
    val reminderTime: String?,
    val isCompletedToday: Boolean,
    val isCompletedSelectedDate: Boolean,
    val completedThisWeekCount: Int,
    val weeklyProgressRatio: Float,
    val weeklyStatus: List<DayCompletionStatus> = emptyList()
)

/**
 * Global dashboard metrics for the habit tracker.
 */
data class HabitTrackerMetrics(
    val totalActiveHabits: Int = 0,
    val completedTodayCount: Int = 0,
    val todayCompletionPercentage: Int = 0,
    val currentStreakRecord: Int = 0,
    val weeklyConsistencyRate: Int = 0
)

/**
 * Overall UI State exposed by [HabitTrackerViewModel].
 */
data class HabitTrackerUiState(
    val habits: List<HabitUiItem> = emptyList(),
    val selectedDate: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    val selectedCategory: String = "Todos",
    val metrics: HabitTrackerMetrics = HabitTrackerMetrics(),
    val isLoading: Boolean = false,
    val userMessage: String? = null
)
