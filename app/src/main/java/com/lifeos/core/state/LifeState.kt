package com.lifeos.core.state

import com.example.features.dashboard.domain.model.DashboardState
import com.example.core.common.Task
import com.example.core.common.Habit
import com.example.core.common.Project
import com.example.core.common.CalendarEvent
import com.example.features.goals.domain.Goal
import com.lifeos.focus.presentation.FocusState

data class TaskState(
    val tasks: List<Task> = emptyList(),
    val filterCategory: String? = null,
    val selectedTaskId: String? = null,
    val isLoading: Boolean = false
)

data class HabitState(
    val habits: List<Habit> = emptyList(),
    val currentStreak: Int = 0,
    val isLoading: Boolean = false
)

data class GoalState(
    val goals: List<Goal> = emptyList(),
    val selectedGoalId: String? = null,
    val isLoading: Boolean = false
)

data class ProjectState(
    val projects: List<Project> = emptyList(),
    val activeProjectId: String? = null,
    val isLoading: Boolean = false
)

data class CalendarState(
    val events: List<CalendarEvent> = emptyList(),
    val selectedDate: String? = null,
    val isLoading: Boolean = false
)

data class AnalyticsState(
    val productivityScore: Int = 85,
    val totalFocusMinutes: Int = 120,
    val completedTasksCount: Int = 12,
    val activeHabitStreak: Int = 7
)

data class AIState(
    val copilotMessage: String = "",
    val isThinking: Boolean = false,
    val recommendations: List<String> = emptyList()
)

data class LifeState(
    val dashboard: DashboardState = DashboardState(),
    val tasks: TaskState = TaskState(),
    val habits: HabitState = HabitState(),
    val goals: GoalState = GoalState(),
    val projects: ProjectState = ProjectState(),
    val calendar: CalendarState = CalendarState(),
    val focus: FocusState = FocusState(),
    val analytics: AnalyticsState = AnalyticsState(),
    val ai: AIState = AIState()
)
