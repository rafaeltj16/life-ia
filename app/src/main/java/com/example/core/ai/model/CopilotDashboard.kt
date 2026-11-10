package com.example.core.ai.model

data class CopilotDashboard(
    val mission: DailyMission?,
    val insights: List<AIInsight>,
    val successRate: Int,
    val lifeScore: Int,
    val momentum: Int,
    val bestFocusHour: Int?,
    val dailyPlan: DailyPlan? = null
)
