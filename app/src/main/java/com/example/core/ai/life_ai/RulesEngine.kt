package com.example.core.ai.life_ai

import com.example.core.ai.engine.AIRulesEngine
import com.example.core.ai.model.AIInsight
import com.example.features.dashboard.domain.model.DashboardData

class RulesEngine {
    fun evaluateRules(dashboard: DashboardData): List<AIInsight> {
        return AIRulesEngine.generateInsights(dashboard)
    }
}
