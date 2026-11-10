package com.example.core.ai.memory

import com.example.features.dashboard.domain.model.DashboardData

data class BehavioralReport(
    val focusPeriodSuggestion: String,
    val primaryStrength: String,
    val primaryWeakness: String,
    val score: Float,
    val warningInsights: List<String> = emptyList()
)

class BehaviorAnalyzer(private val repository: MemoryRepository) {
    private val patternDetector = PatternDetector()

    /**
     * Performs a comprehensive analysis on the user's historical memories and current state.
     */
    fun analyzeUserBehavior(dashboardData: DashboardData): BehavioralReport {
        // Detect new patterns and auto-save them
        val detected = patternDetector.detectPatterns(dashboardData)
        detected.forEach { repository.saveMemory(it) }

        val memories = repository.getMemories()
        
        // Compute productivity score based on habits completion rate and memory strength
        val habitsCompleted = dashboardData.todayHabits.count { it.isCompletedToday }
        val habitsTotal = dashboardData.todayHabits.size
        val habitScore = if (habitsTotal > 0) (habitsCompleted.toFloat() / habitsTotal) * 100f else 82f

        val taskScore = if (dashboardData.todayTasks.isNotEmpty()) {
            (dashboardData.todayTasks.count { it.isCompleted }.toFloat() / dashboardData.todayTasks.size) * 100f
        } else {
            100f
        }

        val baseScore = (habitScore + taskScore) / 2f
        
        // Warnings/Insights list
        val warningInsights = mutableListOf<String>()
        
        var focusPeriod = "Manhã (09:00 - 12:00)"
        var strength = "Leitura diária"
        var weakness = "Frequência de hidratação"

        memories.forEach { memory ->
            val text = memory.fact.lowercase()
            when (memory.category) {
                "Saúde" -> {
                    if (text.contains("exercíc") || text.contains("trein")) {
                        strength = "Consistência nos treinos"
                    }
                    if (text.contains("água") || text.contains("hidrata")) {
                        weakness = "Lembrar de beber água"
                    }
                }
                "Hábito" -> {
                    if (text.contains("ler") || text.contains("leitura")) {
                        strength = "Hábito de leitura consolidado"
                    }
                }
                "Financeiro" -> {
                    if (text.contains("negativo")) {
                        warningInsights.add("Alerta financeiro: Saldo negativo detectado.")
                    }
                }
            }
        }

        // Add warnings from dashboard data
        if (dashboardData.financeSummary.balance < 0) {
            warningInsights.add("Evitar despesas não-essenciais até regularizar o saldo.")
        }

        return BehavioralReport(
            focusPeriodSuggestion = focusPeriod,
            primaryStrength = strength,
            primaryWeakness = weakness,
            score = baseScore,
            warningInsights = warningInsights
        )
    }
}
