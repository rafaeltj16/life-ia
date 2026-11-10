package com.example.features.dashboard.domain

import com.example.core.ai.engine.CopilotEngine
import com.example.core.ai.model.*
import com.example.core.ai.model.Greeting
import com.example.features.dashboard.domain.model.DashboardState

class DashboardUseCase(
    private val copilot: CopilotEngine
) {

    suspend operator fun invoke(
        dashboard: com.example.core.ai.model.DashboardData
    ): CopilotDashboard {
        return copilot.generateDashboard(
            dashboard
        )
    }

    suspend fun getDashboardState(dashboard: com.example.features.dashboard.domain.model.DashboardData): DashboardState {
        val aiDashboardData = com.example.core.ai.model.DashboardData(
            pendingTasks = dashboard.todayTasks.count { !it.isCompleted },
            overdueTasks = 0,
            completedHabitsToday = dashboard.todayHabits.count { it.isCompletedToday },
            totalHabitsToday = dashboard.todayHabits.size,
            currentStreak = 7,
            inactiveProjects = 0,
            waterPercentage = 80
        )
        val copilotDashboard = invoke(aiDashboardData)
        return DashboardState(
            loading = false,
            greeting = Greeting(title = "Bom dia", userName = "Rafael"),
            mission = copilotDashboard.mission,
            tasks = dashboard.todayTasks,
            habits = dashboard.todayHabits,
            events = dashboard.events,
            projects = dashboard.projects,
            lifeScore = copilotDashboard.lifeScore,
            momentum = copilotDashboard.momentum,
            aiRecommendations = copilotDashboard.insights.map { 
                AIRecommendation(
                    title = it.title,
                    description = it.message,
                    confidence = 0.95f,
                    action = it.action
                )
            }
        )
    }
}
