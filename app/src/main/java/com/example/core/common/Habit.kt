package com.example.core.common

import java.util.UUID

data class HabitItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val streak: Int = 0,
    val isCompletedToday: Boolean = false,
    val icon: String = "⚡",
    val category: String = "Geral",
    val targetDaysPerWeek: Int = 7,
    val completedThisWeek: Int = 4,
    val isFirestoreSynced: Boolean = true
) {
    val completedToday: Boolean get() = isCompletedToday
    val weeklyProgressRatio: Float
        get() = if (targetDaysPerWeek > 0) (completedThisWeek.toFloat() / targetDaysPerWeek.toFloat()).coerceIn(0f, 1f) else 0f
}

typealias Habit = HabitItem
