package com.example.services.gemini

import java.util.UUID

/**
 * Data representation of a Daily Mission generated via Gemini API.
 */
data class GeminiDailyMission(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val category: String = "Geral",
    val estimatedMinutes: Int = 30,
    val points: Int = 50,
    val priority: String = "ALTA",
    val isCompleted: Boolean = false
) {
    /**
     * Maps to Copilot module DailyMission for seamless UI consumption.
     */
    fun toCopilotMission(): com.example.features.copilot.data.DailyMission {
        return com.example.features.copilot.data.DailyMission(
            id = id,
            title = title,
            category = category,
            points = points,
            isCompleted = isCompleted,
            priority = priority
        )
    }

    /**
     * Maps to Core AI DailyMission.
     */
    fun toCoreMission(): com.example.core.ai.model.DailyMission {
        val prio = when (priority.uppercase()) {
            "CRÍTICA", "CRITICA" -> com.example.core.ai.model.InsightPriority.CRITICAL
            "ALTA" -> com.example.core.ai.model.InsightPriority.HIGH
            "BAIXA" -> com.example.core.ai.model.InsightPriority.LOW
            else -> com.example.core.ai.model.InsightPriority.MEDIUM
        }
        return com.example.core.ai.model.DailyMission(
            title = title,
            description = description,
            estimatedMinutes = estimatedMinutes,
            priority = prio,
            action = null
        )
    }
}

/**
 * Data representation of a Productivity Insight generated via Gemini API.
 */
data class GeminiProductivityInsight(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val summary: String,
    val actionRecommendation: String,
    val impactScore: Int = 85,
    val category: String = "Produtividade",
    val optimalTimeWindow: String? = null
)

/**
 * Encapsulated user context provided to Gemini API prompts.
 */
data class GeminiUserContext(
    val activeTasks: List<String> = emptyList(),
    val pendingHabits: List<String> = emptyList(),
    val currentGoals: List<String> = emptyList(),
    val scheduleEvents: List<String> = emptyList(),
    val focusScore: Int = 94,
    val streakDays: Int = 18
)
