package com.example.core.ai.engine

import com.example.core.ai.model.*

val InsightPriority.weight: Int
    get() = when (this) {
        InsightPriority.LOW -> 1
        InsightPriority.MEDIUM -> 2
        InsightPriority.HIGH -> 3
        InsightPriority.CRITICAL -> 4
    }

class MissionPlanner {

    fun createMission(
        insights: List<AIInsight>
    ): DailyMission? {

        if (insights.isEmpty()) return null

        val mission = insights
            .sortedByDescending {
                it.priority.weight
            }
            .first()

        return DailyMission(
            title = mission.title,
            description = mission.message,
            estimatedMinutes = estimateTime(mission),
            priority = mission.priority,
            action = mission.action
        )
    }

    private fun estimateTime(
        insight: AIInsight
    ): Int {
        return when (insight.type) {
            AIInsightType.TASK -> 60
            AIInsightType.PROJECT -> 90
            AIInsightType.HABIT -> 20
            AIInsightType.HEALTH -> 15
            AIInsightType.PRODUCTIVITY -> 45
            else -> 30
        }
    }
}
