package com.lifeos.core.context

data class ContextAnalysisResult(
    val focusRecommendation: String,
    val suggestedTaskBreakDuration: Int,
    val highPriorityFocusNeeded: Boolean
)

class ContextAnalyzer {
    fun analyzeSnapshot(snapshot: ContextSnapshot): ContextAnalysisResult {
        val focusNeeded = snapshot.energy >= 7 && snapshot.focusSession != null
        val breakMinutes = if (snapshot.energy < 5) 15 else 5
        val taskTitle = snapshot.currentTask?.title ?: "Atual"
        val recommendation = if (focusNeeded) {
            "Excelente nível de energia (${snapshot.energy}/10). Mantenha o foco na tarefa '$taskTitle'."
        } else {
            "Energia de ${snapshot.energy}/10 com mood ${snapshot.mood ?: Mood.GOOD}. Recomendado fazer uma pausa curta de $breakMinutes min."
        }
        return ContextAnalysisResult(
            focusRecommendation = recommendation,
            suggestedTaskBreakDuration = breakMinutes,
            highPriorityFocusNeeded = focusNeeded
        )
    }
}

