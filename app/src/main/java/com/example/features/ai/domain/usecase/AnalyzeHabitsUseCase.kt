package com.example.features.ai.domain.usecase

import com.example.core.ai.model.AIInsight
import com.example.core.ai.model.InsightPriority
import com.example.core.ai.model.AIInsightType
import com.example.core.ai.model.AIAction
import com.example.core.ai.model.UserContext

class AnalyzeHabitsUseCase {
    operator fun invoke(userContext: UserContext): List<AIInsight> {
        val insights = mutableListOf<AIInsight>()
        val todayHabits = userContext.dashboardData.todayHabits
        val completedCount = todayHabits.count { it.isCompletedToday }
        val totalCount = todayHabits.size
        
        if (totalCount > 0) {
            val completionRate = completedCount.toFloat() / totalCount
            if (completionRate == 1.0f) {
                insights.add(
                    AIInsight(
                        id = "habit_perfect_day",
                        title = "Performance Impecável!",
                        message = "Você completou 100% dos seus hábitos hoje. Excelente trabalho de disciplina!",
                        type = AIInsightType.HABIT,
                        priority = InsightPriority.HIGH,
                        action = null,
                        createdAt = System.currentTimeMillis()
                    )
                )
            } else if (completionRate >= 0.5f) {
                insights.add(
                    AIInsight(
                        id = "habit_good_pace",
                        title = "Bom Progresso nos Hábitos",
                        message = "Você completou $completedCount de $totalCount hábitos hoje. Continue assim!",
                        type = AIInsightType.HABIT,
                        priority = InsightPriority.MEDIUM,
                        action = null,
                        createdAt = System.currentTimeMillis()
                    )
                )
            } else {
                insights.add(
                    AIInsight(
                        id = "habit_needs_focus",
                        title = "Foque na Consistência",
                        message = "Apenas $completedCount de $totalCount hábitos concluídos. Tente começar pelo menor hábito agora.",
                        type = AIInsightType.HABIT,
                        priority = InsightPriority.HIGH,
                        action = AIAction(
                            id = "action_start_habit",
                            label = "Praticar Hábito Pequeno",
                            type = "HABIT",
                            payloadTitle = "Micro-Meditação de 2 min",
                            payloadIcon = "🧘"
                        ),
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
        } else {
            insights.add(
                AIInsight(
                    id = "habit_no_habits",
                    title = "Nenhum Hábito Cadastrado",
                    message = "Crie rotinas diárias para desenvolver consistência em alta performance.",
                    type = AIInsightType.HABIT,
                    priority = InsightPriority.MEDIUM,
                    action = AIAction(
                        id = "action_add_habit",
                        label = "Criar Meu Primeiro Hábito",
                        type = "HABIT",
                        payloadTitle = "Beber 2L de Água Diários",
                        payloadIcon = "💧"
                    ),
                    createdAt = System.currentTimeMillis()
                )
            )
        }
        return insights
    }
}
