package com.lifeos.core.ai.models

import com.example.core.common.CalendarEvent
import com.example.core.common.Habit
import com.example.core.common.Task
import com.example.features.goals.domain.Goal
import com.lifeos.focus.domain.model.FocusSession
import java.time.LocalDateTime

data class AIContext(
    val currentTime: LocalDateTime = LocalDateTime.now(),
    val lifeScore: Int = 75,
    val activeGoal: Goal? = null,
    val todayTasks: List<Task> = emptyList(),
    val todayHabits: List<Habit> = emptyList(),
    val calendarEvents: List<CalendarEvent> = emptyList(),
    val activeFocusSession: FocusSession? = null,
    val productivityScore: Int = 80
)
