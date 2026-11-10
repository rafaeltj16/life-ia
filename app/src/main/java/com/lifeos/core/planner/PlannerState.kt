package com.lifeos.core.planner

import com.example.core.common.CalendarEvent
import com.example.core.common.Task
import com.example.features.goals.domain.Goal
import com.lifeos.focus.domain.model.FocusSession

data class PlannedTimeSlot(
    val id: String,
    val title: String,
    val startTime: String,
    val durationMinutes: Int,
    val isFocusBlock: Boolean = false,
    val priorityScore: Float = 0.5f,
    val isConflict: Boolean = false
)

data class PlannerState(
    val todayTasks: List<Task> = emptyList(),
    val availableMinutes: Int = 105,
    val occupiedMinutes: Int = 180,
    val focusSessions: List<FocusSession> = emptyList(),
    val calendarEvents: List<CalendarEvent> = emptyList(),
    val activeGoals: List<Goal> = emptyList(),
    val dailyPlan: List<PlannedTimeSlot> = emptyList(),
    val isOptimized: Boolean = true
)

