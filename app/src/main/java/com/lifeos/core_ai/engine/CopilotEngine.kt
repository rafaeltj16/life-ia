package com.lifeos.core_ai.engine

import com.lifeos.core_ai.decision.DecisionEngine
import com.lifeos.core_ai.decision.DecisionType
import com.lifeos.core_ai.orchestrator.GeminiOrchestrator
import com.example.core.ai.engine.AIRulesEngine
import com.lifeos.core_ai.model.DashboardSnapshot
import com.example.core.ai.model.CopilotDashboard
import com.example.core.ai.model.DashboardData
import com.example.core.ai.model.DailyMission
import com.example.core.ai.model.DailyPlan
import com.example.core.ai.model.AIInsight

class CopilotEngine(
    private val decisionEngine: DecisionEngine,
    private val gemini: GeminiOrchestrator,
    private val rules: AIRulesEngine
) {

    suspend fun generate(
        snapshot: DashboardSnapshot
    ): CopilotDashboard {
        val decision =
            decisionEngine.evaluate(snapshot)
        return when(decision.type){
            DecisionType.LOCAL -> {
                generateLocal(snapshot)
            }
            DecisionType.GEMINI -> {
                generateGemini(snapshot)
            }
            DecisionType.HYBRID -> {
                generateHybrid(snapshot)
            }
            else -> {
                generateLocal(snapshot)
            }
        }
    }

    private fun generateLocal(snapshot: DashboardSnapshot): CopilotDashboard {
        val coreDashboardData = DashboardData(
            pendingTasks = snapshot.tasks.count { !it.isCompleted },
            overdueTasks = snapshot.tasks.count { !it.isCompleted && it.dueDate == "Atrasadas" },
            completedHabitsToday = snapshot.habits.count { it.isCompletedToday },
            totalHabitsToday = snapshot.habits.size,
            currentStreak = snapshot.habits.maxOfOrNull { it.streak } ?: 0,
            inactiveProjects = snapshot.projects.size,
            waterPercentage = 60
        )
        val insights = rules.generateInsights(coreDashboardData)
        val localMission = DailyMission(
            title = "Foco Local",
            description = "Realizar tarefas pendentes prioritárias",
            estimatedMinutes = 60,
            priority = com.example.core.ai.model.InsightPriority.HIGH,
            action = null
        )
        val localPlan = DailyPlan(
            generatedAt = System.currentTimeMillis(),
            mission = localMission,
            blocks = emptyList(),
            estimatedProductivity = 80,
            recommendations = emptyList()
        )
        return CopilotDashboard(
            mission = localMission,
            insights = insights,
            successRate = 85,
            lifeScore = 75,
            momentum = 5,
            bestFocusHour = 9,
            dailyPlan = localPlan
        )
    }

    private suspend fun generateGemini(snapshot: DashboardSnapshot): CopilotDashboard {
        val response = gemini.generateDailyPlan(snapshot)
        val coreDashboardData = DashboardData(
            pendingTasks = snapshot.tasks.count { !it.isCompleted },
            overdueTasks = snapshot.tasks.count { !it.isCompleted && it.dueDate == "Atrasadas" },
            completedHabitsToday = snapshot.habits.count { it.isCompletedToday },
            totalHabitsToday = snapshot.habits.size,
            currentStreak = snapshot.habits.maxOfOrNull { it.streak } ?: 0,
            inactiveProjects = snapshot.projects.size,
            waterPercentage = 60
        )
        val insights = rules.generateInsights(coreDashboardData)
        val geminiMission = DailyMission(
            title = "Missão Gemini",
            description = response.text.take(100),
            estimatedMinutes = 120,
            priority = com.example.core.ai.model.InsightPriority.HIGH,
            action = null
        )
        val geminiPlan = DailyPlan(
            generatedAt = System.currentTimeMillis(),
            mission = geminiMission,
            blocks = emptyList(),
            estimatedProductivity = 90,
            recommendations = emptyList()
        )
        return CopilotDashboard(
            mission = geminiMission,
            insights = insights,
            successRate = 90,
            lifeScore = 80,
            momentum = 8,
            bestFocusHour = 10,
            dailyPlan = geminiPlan
        )
    }

    private suspend fun generateHybrid(snapshot: DashboardSnapshot): CopilotDashboard {
        val response = gemini.generateDailyPlan(snapshot)
        val coreDashboardData = DashboardData(
            pendingTasks = snapshot.tasks.count { !it.isCompleted },
            overdueTasks = snapshot.tasks.count { !it.isCompleted && it.dueDate == "Atrasadas" },
            completedHabitsToday = snapshot.habits.count { it.isCompletedToday },
            totalHabitsToday = snapshot.habits.size,
            currentStreak = snapshot.habits.maxOfOrNull { it.streak } ?: 0,
            inactiveProjects = snapshot.projects.size,
            waterPercentage = 60
        )
        val insights = rules.generateInsights(coreDashboardData)
        val hybridMission = DailyMission(
            title = "Missão Híbrida",
            description = "Equilíbrio entre regras locais e IA",
            estimatedMinutes = 90,
            priority = com.example.core.ai.model.InsightPriority.HIGH,
            action = null
        )
        val hybridPlan = DailyPlan(
            generatedAt = System.currentTimeMillis(),
            mission = hybridMission,
            blocks = emptyList(),
            estimatedProductivity = 95,
            recommendations = emptyList()
        )
        return CopilotDashboard(
            mission = hybridMission,
            insights = insights,
            successRate = 95,
            lifeScore = 85,
            momentum = 10,
            bestFocusHour = 11,
            dailyPlan = hybridPlan
        )
    }

}
