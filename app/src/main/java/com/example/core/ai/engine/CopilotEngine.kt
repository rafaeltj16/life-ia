package com.example.core.ai.engine

import com.example.core.ai.life_ai.MemoryEngine
import com.example.core.ai.model.*
import com.example.core.ai.prediction.PredictionEngine
import com.example.core.ai.score.*

class CopilotEngine(
    private val rulesEngine: AIRulesEngine,
    private val predictionEngine: PredictionEngine,
    private val memoryEngine: MemoryEngine,
    private val lifeScoreCalculator: LifeScoreCalculator,
    private val momentumCalculator: MomentumCalculator,
    private val focusScoreCalculator: FocusScoreCalculator,
    private val plannerEngine: PlannerEngine
) {

    suspend fun generateDashboard(
        dashboard: DashboardData
    ): CopilotDashboard {

        val profile = memoryEngine.profile()

        val insights = rulesEngine.generateInsights(dashboard)

        val mission = DailyMissionGenerator()
            .generate(insights)

        val plan = mission?.let {
            plannerEngine.createPlan(
                mission = it,
                insights = insights,
                bestFocusHour = profile.bestFocusTime?.hour
            )
        }

        val lifeScore =
            lifeScoreCalculator.calculate(dashboard)

        val successRate =
            predictionEngine.predictSuccessRate(dashboard)

        return CopilotDashboard(
            mission = mission,
            insights = insights,
            lifeScore = lifeScore,
            momentum = profile.currentMomentum,
            successRate = successRate,
            bestFocusHour = profile.bestFocusTime?.hour,
            dailyPlan = plan
        )

    }

    suspend fun generate(
        snapshot: com.lifeos.core_ai.model.DashboardSnapshot
    ): CopilotDashboard {
        val coreDashboardData = DashboardData(
            pendingTasks = snapshot.tasks.count { !it.isCompleted },
            overdueTasks = snapshot.tasks.count { !it.isCompleted && it.dueDate == "Atrasadas" },
            completedHabitsToday = snapshot.habits.count { it.isCompletedToday },
            totalHabitsToday = snapshot.habits.size,
            currentStreak = snapshot.habits.maxOfOrNull { it.streak } ?: 0,
            inactiveProjects = snapshot.projects.size,
            waterPercentage = 60
        )
        return generateDashboard(coreDashboardData)
    }

}
