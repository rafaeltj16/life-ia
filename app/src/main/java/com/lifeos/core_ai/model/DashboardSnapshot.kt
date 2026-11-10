package com.lifeos.core_ai.model

import com.lifeos.tasks.domain.model.Task
import com.lifeos.habits.domain.model.Habit
import com.lifeos.projects.domain.model.Project
import com.lifeos.calendar.domain.model.CalendarEvent

data class DashboardSnapshot(
    val tasks: List<Task>,
    val habits: List<Habit>,
    val projects: List<Project>,
    val events: List<CalendarEvent>,
    val generatedAt: Long
)
