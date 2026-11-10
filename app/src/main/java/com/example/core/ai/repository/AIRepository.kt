package com.example.core.ai.repository

import com.example.core.common.AgendaEvent
import com.example.core.common.GoalItem
import com.example.core.common.HabitItem
import com.example.core.common.TaskItem
import com.example.features.dashboard.domain.model.DashboardData
import java.util.UUID

data class AIMemoryItem(
    val id: String = UUID.randomUUID().toString(),
    val fact: String,
    val category: String = "Geral"
)

data class AIPlanItem(
    val id: String = UUID.randomUUID().toString(),
    val time: String,
    val activity: String,
    val category: String,
    val priority: String = "Média",
    val icon: String = "📅"
)

data class AIRecommendation(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val category: String, // "Hábito", "Tarefa", "Financeiro", "Saúde", "Horário Ótimo", "Produtividade"
    val icon: String,
    val actionText: String = "Adicionar",
    val type: RecommendationType,
    val optimalTime: String? = null,
    val productivityImpact: String? = null,
    val reason: String? = null,
    val targetData: String? = null
)

data class AIAnalysisReport(
    val analysisText: String = "",
    val insights: List<String> = emptyList(),
    val recommendations: List<AIRecommendation> = emptyList(),
    val optimalTimeRecommendations: List<AIRecommendation> = emptyList(),
    val productivityImprovements: List<AIRecommendation> = emptyList(),
    val habitSuggestions: List<AIRecommendation> = emptyList()
)

enum class RecommendationType {
    HABIT, TASK, FINANCE, HEALTH, OPTIMAL_TIME, PRODUCTIVITY
}

interface AIRepository {
    suspend fun getMemories(): List<AIMemoryItem>
    suspend fun saveMemory(memory: AIMemoryItem)
    suspend fun deleteMemory(id: String)
    
    suspend fun generateDailyPlan(memories: List<AIMemoryItem>, habits: List<String>, tasks: List<String>): List<AIPlanItem>
    suspend fun getRecommendations(memories: List<AIMemoryItem>): List<AIRecommendation>
    suspend fun runLifeAIEngine(dashboardData: DashboardData, memories: List<AIMemoryItem>): AIAnalysisReport

    suspend fun generateInsights(
        dashboard: com.example.core.ai.model.DashboardData
    ): List<com.example.core.ai.model.AIInsight>

    suspend fun generatePersonalizedSuggestions(
        schedule: List<AgendaEvent>,
        tasks: List<TaskItem>,
        habits: List<HabitItem>,
        goals: List<GoalItem>,
        memories: List<AIMemoryItem>
    ): AIAnalysisReport
}
