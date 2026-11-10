package com.example.core.ai.model

import com.example.core.ai.repository.AIMemoryItem
import com.example.features.dashboard.domain.model.DashboardData

data class UserContext(
    val productivityScore: Int,
    val lifeScore: Int,
    val momentum: Int,
    val completedHabits: Int,
    val completedTasks: Int,
    val currentStreak: Int,
    val bestFocusHour: Int,
    val worstFocusHour: Int,
    val dashboardData: DashboardData = DashboardData(),
    val memories: List<AIMemoryItem> = emptyList(),
    val melhorHorarioProdutividade: String = "09h às 11h",
    val habitosMaisFortes: List<String> = listOf("Academia"),
    val habitosMaisFracos: List<String> = listOf("Leitura"),
    val tempoMedioFoco: Int = 25, // em minutos
    val diasMaisProdutivos: List<String> = listOf("terça-feira"),
    val objetivoPrincipal: String = "Equilíbrio e Consistência"
) {
    // Secondary constructor to allow creation with only the requested fields (with legacy fields defaulted)
    constructor(
        productivityScore: Int,
        lifeScore: Int,
        momentum: Int,
        completedHabits: Int,
        completedTasks: Int,
        currentStreak: Int,
        bestFocusHour: Int,
        worstFocusHour: Int
    ) : this(
        productivityScore = productivityScore,
        lifeScore = lifeScore,
        momentum = momentum,
        completedHabits = completedHabits,
        completedTasks = completedTasks,
        currentStreak = currentStreak,
        bestFocusHour = bestFocusHour,
        worstFocusHour = worstFocusHour,
        dashboardData = DashboardData(),
        memories = emptyList(),
        melhorHorarioProdutividade = "Manhã",
        habitosMaisFortes = emptyList(),
        habitosMaisFracos = emptyList(),
        tempoMedioFoco = 25,
        diasMaisProdutivos = emptyList(),
        objetivoPrincipal = "Equilíbrio e Consistência"
    )

    companion object {
        fun from(dashboardData: DashboardData, memories: List<AIMemoryItem>): UserContext {
            // 1. Melhor horário de produtividade
            val melhorHorario = memories.find { it.fact.contains("manhã", ignoreCase = true) || it.fact.contains("cedo", ignoreCase = true) }?.let { "09h às 11h" }
                ?: memories.find { it.fact.contains("tarde", ignoreCase = true) }?.let { "14h às 16h" }
                ?: memories.find { it.fact.contains("noite", ignoreCase = true) || it.fact.contains("dormir", ignoreCase = true) }?.let { "20h às 22h" }
                ?: "09h às 11h"

            // 2. Hábitos mais fortes e mais fracos
            val rates = dashboardData.weeklyStats.rates
            val fortes = rates.entries
                .filter { it.value >= 0.6f }
                .sortedByDescending { it.value }
                .map { it.key }
                .take(2)
                .ifEmpty { 
                    dashboardData.todayHabits.filter { it.isCompletedToday }.map { it.name }.take(2)
                        .ifEmpty { listOf("Academia") }
                }

            val fracos = rates.entries
                .filter { it.value < 0.6f }
                .sortedBy { it.value }
                .map { it.key }
                .take(2)
                .ifEmpty { 
                    dashboardData.todayHabits.filter { !it.isCompletedToday }.map { it.name }.take(2)
                        .ifEmpty { listOf("Leitura") }
                }

            // 3. Tempo médio de foco
            val focusMemory = memories.find { it.fact.contains("foco", ignoreCase = true) || it.fact.contains("pomodoro", ignoreCase = true) || it.fact.contains("minutos", ignoreCase = true) }
            val tempoFoco = focusMemory?.let {
                Regex("\\d+").find(it.fact)?.value?.toIntOrNull()
            } ?: 25

            // 4. Dias mais produtivos
            val dias = listOf("terça-feira")

            // 5. Objetivo principal
            val objetivoRaw = memories.find { 
                it.fact.contains("meta", ignoreCase = true) || 
                it.fact.contains("objetivo", ignoreCase = true) || 
                it.fact.contains("quero", ignoreCase = true) ||
                it.fact.contains("principal", ignoreCase = true) 
            }?.fact
            
            val objetivo = if (objetivoRaw != null) {
                var clean = objetivoRaw.replace(Regex("(?i)minha principal meta é|meu objetivo principal é|quero|meu objetivo é"), "").trim()
                if (clean.length > 50) {
                    clean = clean.take(47) + "..."
                }
                clean.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            } else {
                "Consistência em alta performance e hábitos saudáveis"
            }

            // Calculations for new properties
            val totalHabits = dashboardData.todayHabits.size
            val compHabitsCount = dashboardData.todayHabits.count { it.isCompletedToday }
            val totalTasks = dashboardData.todayTasks.size
            val compTasksCount = dashboardData.todayTasks.count { it.isCompleted }
            
            val habitRatio = if (totalHabits > 0) compHabitsCount.toFloat() / totalHabits else 0.5f
            val taskRatio = if (totalTasks > 0) compTasksCount.toFloat() / totalTasks else 0.5f
            val prodScore = ((habitRatio + taskRatio) / 2f * 100).toInt().coerceIn(0, 100)

            val lifeScoreVal = (70 + (dashboardData.level.coerceIn(1, 20) * 1.5).toInt() + (dashboardData.streak.coerceIn(0, 30) * 0.5).toInt()).coerceIn(0, 100)
            val momentumVal = (50 + dashboardData.streak * 2 + compHabitsCount * 5).coerceIn(0, 100)

            val bestFocusHourVal = when (melhorHorario) {
                "Manhã" -> 9
                "Tarde" -> 15
                "Noite" -> 21
                else -> 9
            }
            val worstFocusHourVal = when (bestFocusHourVal) {
                9 -> 14
                15 -> 20
                21 -> 14
                else -> 14
            }

            return UserContext(
                productivityScore = prodScore,
                lifeScore = lifeScoreVal,
                momentum = momentumVal,
                completedHabits = compHabitsCount,
                completedTasks = compTasksCount,
                currentStreak = dashboardData.streak,
                bestFocusHour = bestFocusHourVal,
                worstFocusHour = worstFocusHourVal,
                dashboardData = dashboardData,
                memories = memories,
                melhorHorarioProdutividade = melhorHorario,
                habitosMaisFortes = fortes,
                habitosMaisFracos = fracos,
                tempoMedioFoco = tempoFoco,
                diasMaisProdutivos = dias,
                objetivoPrincipal = objetivo
            )
        }
    }
}
