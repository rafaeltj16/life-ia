package com.lifeos.core.context

import java.time.DayOfWeek
import java.time.LocalTime

enum class ProductivityPattern {
    MORNING_LARK,
    NIGHT_OWL,
    BALANCED,
    BURST_WORKER
}

data class UserProfile(
    val preferredFocusTime: LocalTime = LocalTime.of(9, 0),
    val averageTaskDuration: Int = 45,
    val preferredWorkDays: List<DayOfWeek> = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY
    ),
    val productivityPattern: ProductivityPattern = ProductivityPattern.MORNING_LARK,
    val favoriteCategories: List<String> = listOf("Trabalho", "Estudos", "Saúde")
)

