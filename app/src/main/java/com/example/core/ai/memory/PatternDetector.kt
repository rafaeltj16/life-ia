package com.example.core.ai.memory

import com.example.features.dashboard.domain.model.DashboardData

class PatternDetector {

    fun detectBestHour(
        events: List<UserEvent>
    ): Int? {

        return events
            .filterIsInstance<UserEvent.TaskCompleted>()
            .groupBy {
                it.completedAt.atZone(
                    java.time.ZoneId.systemDefault()
                ).hour
            }
            .maxByOrNull {
                it.value.size
            }?.key

    }

    fun detectPatterns(dashboardData: DashboardData): List<UserMemory> {
        val patterns = mutableListOf<UserMemory>()

        // 1. Balance check
        if (dashboardData.financeSummary.balance < 0) {
            patterns.add(
                UserMemory(
                    fact = "Saldo negativo na conta bancária",
                    category = "Financeiro",
                    strength = 5,
                    confidence = 1.0f
                )
            )
        }

        // 2. High habit completion
        val totalHabits = dashboardData.todayHabits.size
        val completedHabits = dashboardData.todayHabits.count { it.isCompletedToday }
        if (totalHabits > 0 && (completedHabits.toFloat() / totalHabits) >= 0.8f) {
            patterns.add(
                UserMemory(
                    fact = "Excelente taxa de conclusão de hábitos",
                    category = "Hábito",
                    strength = 3,
                    confidence = 0.9f
                )
            )
        }

        // 3. Physical Exercise habit completion
        val workoutHabits = dashboardData.todayHabits.filter { 
            it.name.lowercase().contains("exercíc") || 
            it.name.lowercase().contains("treino") ||
            it.name.lowercase().contains("academia")
        }
        if (workoutHabits.isNotEmpty() && workoutHabits.all { it.isCompletedToday }) {
            patterns.add(
                UserMemory(
                    fact = "Consistência nos treinos físicos e exercícios",
                    category = "Saúde",
                    strength = 4,
                    confidence = 0.95f
                )
            )
        }

        // 4. Reading habit completion
        val readingHabits = dashboardData.todayHabits.filter {
            it.name.lowercase().contains("ler") || 
            it.name.lowercase().contains("leitura")
        }
        if (readingHabits.isNotEmpty() && readingHabits.all { it.isCompletedToday }) {
            patterns.add(
                UserMemory(
                    fact = "Hábito de leitura diária mantido",
                    category = "Hábito",
                    strength = 3,
                    confidence = 0.9f
                )
            )
        }

        return patterns
    }
}

