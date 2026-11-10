package com.example.core.ai.life_ai

import com.example.core.ai.prompt.PromptBuilder as OldPromptBuilder
import com.example.core.ai.repository.AIMemoryItem
import com.example.features.dashboard.domain.model.DashboardData

class PromptBuilder {
    fun buildDailyPlanPrompt(
        memories: List<AIMemoryItem>,
        habits: List<String>,
        tasks: List<String>
    ): String {
        return OldPromptBuilder.buildDailyPlanPrompt(memories, habits, tasks)
    }

    fun buildRecommendationsPrompt(memories: List<AIMemoryItem>): String {
        return OldPromptBuilder.buildRecommendationsPrompt(memories)
    }

    fun buildLifeAIEnginePrompt(
        dashboardData: DashboardData,
        memories: List<AIMemoryItem>
    ): String {
        return OldPromptBuilder.buildLifeAIEnginePrompt(dashboardData, memories)
    }
}
