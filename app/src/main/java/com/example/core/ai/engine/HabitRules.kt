package com.example.core.ai.engine

import com.example.core.ai.model.AIAction
import com.example.core.ai.model.AIInsight
import com.example.core.ai.model.AIInsightType
import com.example.core.ai.model.InsightPriority
import com.example.core.ai.model.UserContext

object HabitRules {
    fun evaluateHabits(userContext: UserContext): List<AIInsight> {
        val data = userContext.dashboardData
        val total = data.todayHabits.size
        val completed = data.todayHabits.count { it.isCompletedToday }
        val pending = total - completed
        
        val list = mutableListOf<AIInsight>()
        if (total > 0) {
            if (pending == 0) {
                list.add(
                    AIInsight(
                        id = "rule_habits_perfect",
                        title = "Hábitos Concluídos!",
                        message = "Incrível! Todos os seus $total hábitos de hoje foram preenchidos. Continue essa sequência fantástica!",
                        type = AIInsightType.HABIT,
                        priority = InsightPriority.HIGH,
                        action = null,
                        createdAt = System.currentTimeMillis()
                    )
                )
            } else {
                list.add(
                    AIInsight(
                        id = "rule_habits_pending",
                        title = "Hábitos Pendentes",
                        message = "Você possui $pending hábitos restantes hoje. Comece com pequenas vitórias para manter o foco!",
                        type = AIInsightType.HABIT,
                        priority = InsightPriority.MEDIUM,
                        action = AIAction(
                            id = "action_habits_view",
                            label = "Ver Hábitos",
                            type = "HABIT",
                            payloadTitle = "Completar hábitos pendentes",
                            payloadIcon = "🧘"
                        ),
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
        }
        return list
    }

    fun evaluateStreakRule(userContext: UserContext): AIInsight? {
        val data = userContext.dashboardData
        return if (data.streak >= 5) {
            AIInsight(
                id = "rule_streak_high",
                title = "Sequência Incrível!",
                message = "Você está em uma sequência de ${data.streak} dias de hábitos! Continue alimentando esse momento fantástico.",
                type = AIInsightType.MOTIVATION,
                priority = InsightPriority.HIGH,
                action = null,
                createdAt = System.currentTimeMillis()
            )
        } else {
            null
        }
    }
}
