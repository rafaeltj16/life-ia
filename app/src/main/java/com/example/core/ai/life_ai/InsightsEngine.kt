package com.example.core.ai.life_ai

import android.content.Context
import com.example.core.ai.model.AIInsight
import com.example.core.ai.model.UserContext
import com.example.core.ai.repository.AIRepository
import com.example.features.ai.data.repository.AIRepositoryImpl
import com.example.features.ai.domain.usecase.GenerateInsightsUseCase

class InsightsEngine(context: Context) {
    private val repository: AIRepository = AIRepositoryImpl(context)
    private val useCase = GenerateInsightsUseCase(repository)

    suspend fun getInsights(userContext: UserContext): List<AIInsight> {
        return useCase(userContext)
    }

    suspend fun runLifeAIEngineDirect(
        dashboardData: com.example.features.dashboard.domain.model.DashboardData,
        memories: List<com.example.core.ai.repository.AIMemoryItem>
    ): com.example.core.ai.repository.AIAnalysisReport {
        return repository.runLifeAIEngine(dashboardData, memories)
    }
}
