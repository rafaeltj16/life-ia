package com.example.features.ai.domain.usecase

import com.example.core.ai.model.AIInsight
import com.example.core.ai.model.InsightPriority
import com.example.core.ai.model.AIInsightType
import com.example.core.ai.model.UserContext
import com.example.core.ai.repository.AIRepository
import com.example.core.ai.engine.AIRulesEngine

class GenerateInsightsUseCase(
    private val repository: AIRepository,
    private val analyzeHabitsUseCase: AnalyzeHabitsUseCase = AnalyzeHabitsUseCase(),
    private val analyzeTasksUseCase: AnalyzeTasksUseCase = AnalyzeTasksUseCase()
) {
    suspend operator fun invoke(userContext: UserContext): List<AIInsight> {
        val staticInsights = analyzeHabitsUseCase(userContext) + 
                             analyzeTasksUseCase(userContext) + 
                             AIRulesEngine.processRules(userContext)
        
        return try {
            val report = repository.runLifeAIEngine(userContext.dashboardData, userContext.memories)
            val dynamicInsights = report.insights.mapIndexed { index, insightStr ->
                AIInsight(
                    id = "dyn_insight_$index",
                    title = "Insight Estratégico",
                    message = insightStr,
                    type = AIInsightType.PRODUCTIVITY,
                    priority = InsightPriority.HIGH,
                    action = null,
                    createdAt = System.currentTimeMillis()
                )
            }
            staticInsights + dynamicInsights
        } catch (e: Exception) {
            staticInsights
        }
    }
}
