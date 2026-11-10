package com.example.features.dashboard.domain.model

import com.example.core.ai.model.Greeting
import com.example.core.ai.model.DailyMission
import com.example.core.ai.model.AIRecommendation
import com.example.core.common.Task
import com.example.core.common.Habit
import com.example.core.common.CalendarEvent
import com.example.core.common.Project

data class DashboardState(
    val loading: Boolean = false,
    val greeting: Greeting? = null,
    val mission: DailyMission? = null,
    val tasks: List<Task> = emptyList(),
    val habits: List<Habit> = emptyList(),
    val events: List<CalendarEvent> = emptyList(),
    val projects: List<Project> = emptyList(),
    val lifeScore: Int = 0,
    val momentum: Int = 0,
    val aiRecommendations: List<AIRecommendation> = emptyList(),
    val error: String? = null
)

