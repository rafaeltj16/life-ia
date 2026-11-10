package com.example.features.dashboard.domain.model

import com.example.core.ai.model.AIInsight
import com.example.core.ai.model.DailyMission

data class DashboardAIState(
    val mission: DailyMission? = null,
    val insights: List<AIInsight> = emptyList(),
    val loading: Boolean = false
)
