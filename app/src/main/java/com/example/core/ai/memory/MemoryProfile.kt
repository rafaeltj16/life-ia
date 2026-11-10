package com.example.core.ai.memory

import java.time.DayOfWeek
import java.time.LocalTime

data class MemoryProfile(
    val bestFocusTime: LocalTime? = null,
    val worstFocusTime: LocalTime? = null,
    val mostProductiveDay: DayOfWeek? = null,
    val leastProductiveDay: DayOfWeek? = null,
    val averageCompletedTasks: Double = 0.0,
    val averageCompletedHabits: Double = 0.0,
    val currentLifeScore: Int = 0,
    val currentMomentum: Int = 0
)
