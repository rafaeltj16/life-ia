package com.lifeos.core.context

import com.example.core.common.CalendarEvent
import com.example.core.common.Habit
import com.example.core.common.Project
import com.example.core.common.Task
import com.example.features.goals.domain.Goal
import com.lifeos.focus.domain.model.FocusSession
import java.time.Instant

enum class Mood {
    GREAT,
    GOOD,
    NEUTRAL,
    TIRED,
    STRESSED
}

data class ContextSnapshot(
    val timestamp: Instant = Instant.now(),
    val activeGoal: Goal? = null,
    val activeProject: Project? = null,
    val currentTask: Task? = null,
    val todayHabits: List<Habit> = emptyList(),
    val calendarEvents: List<CalendarEvent> = emptyList(),
    val focusSession: FocusSession? = null,
    val lifeScore: Int = 92,
    val mood: Mood? = Mood.GREAT,
    val energy: Int = 8
)

