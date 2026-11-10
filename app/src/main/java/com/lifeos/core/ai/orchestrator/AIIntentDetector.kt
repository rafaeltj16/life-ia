package com.lifeos.core.ai.orchestrator

enum class AIIntent {
    CREATE_TASK,
    START_FOCUS,
    CHECK_HABITS,
    CALCULATE_SCORE,
    GENERAL_QUERY
}

class AIIntentDetector {
    fun detectIntent(query: String): AIIntent {
        val lower = query.lowercase()
        return when {
            lower.contains("tarefa") || lower.contains("task") -> AIIntent.CREATE_TASK
            lower.contains("foco") || lower.contains("focus") -> AIIntent.START_FOCUS
            lower.contains("hábito") || lower.contains("habit") -> AIIntent.CHECK_HABITS
            lower.contains("score") || lower.contains("pontuação") -> AIIntent.CALCULATE_SCORE
            else -> AIIntent.GENERAL_QUERY
        }
    }
}
