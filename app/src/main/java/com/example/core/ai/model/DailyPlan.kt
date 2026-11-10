package com.example.core.ai.model

import java.time.LocalTime

data class DailyPlan(
    val generatedAt: Long,
    val mission: DailyMission,
    val blocks: List<TimeBlock>,
    val estimatedProductivity: Int,
    val recommendations: List<AIRecommendation>
)
