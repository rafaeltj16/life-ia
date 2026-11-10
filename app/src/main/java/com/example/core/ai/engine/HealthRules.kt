package com.example.core.ai.engine

import com.example.core.ai.model.AIAction
import com.example.core.ai.model.AIInsight
import com.example.core.ai.model.AIInsightType
import com.example.core.ai.model.InsightPriority
import com.example.core.ai.model.UserContext

object HealthRules {
    fun evaluateHealth(userContext: UserContext): List<AIInsight> {
        val data = userContext.dashboardData
        val list = mutableListOf<AIInsight>()
        val hasWaterHabit = data.todayHabits.any { it.name.contains("água", ignoreCase = true) || it.name.contains("agua", ignoreCase = true) }
        val waterHabitCompleted = data.todayHabits.any { (it.name.contains("água", ignoreCase = true) || it.name.contains("agua", ignoreCase = true)) && it.isCompletedToday }
        
        if (hasWaterHabit && !waterHabitCompleted) {
            list.add(
                AIInsight(
                    id = "rule_health_water_pending",
                    title = "Hora de se Hidratar",
                    message = "Você ainda não marcou o hábito de beber água hoje. A hidratação é vital para sua clareza mental e energia.",
                    type = AIInsightType.HEALTH,
                    priority = InsightPriority.MEDIUM,
                    action = AIAction(
                        id = "action_drink_water",
                        label = "Beber Água",
                        type = "HEALTH",
                        payloadTitle = "Registrar Copo de Água",
                        payloadIcon = "💧"
                    ),
                    createdAt = System.currentTimeMillis()
                )
            )
        } else {
            list.add(
                AIInsight(
                    id = "rule_health_break",
                    title = "Pausa Ativa Recomendada",
                    message = "Mantenha-se saudável: faça uma pausa de 5 minutos a cada hora de foco intenso para se alongar e descansar a vista.",
                    type = AIInsightType.HEALTH,
                    priority = InsightPriority.LOW,
                    action = null,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
        return list
    }
}
