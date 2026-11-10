package com.example.core.ai.engine

import com.example.core.ai.model.AIInsight
import com.example.core.ai.model.AIInsightType
import com.example.core.ai.model.InsightPriority
import com.example.core.ai.model.UserContext

object CalendarRules {
    fun evaluateAgenda(userContext: UserContext): List<AIInsight> {
        val data = userContext.dashboardData
        val list = mutableListOf<AIInsight>()
        val eventsCount = data.events.size
        
        if (eventsCount > 0) {
            list.add(
                AIInsight(
                    id = "rule_agenda_events",
                    title = "Compromissos Agendados",
                    message = "Você possui $eventsCount compromisso(s) na sua agenda hoje. Reserve blocos de tempo livre para trabalhar com foco profundo.",
                    type = AIInsightType.CALENDAR,
                    priority = InsightPriority.MEDIUM,
                    action = null,
                    createdAt = System.currentTimeMillis()
                )
            )
        } else {
            list.add(
                AIInsight(
                    id = "rule_agenda_free",
                    title = "Dia Livre de Reuniões",
                    message = "Nenhum evento agendado para hoje! Excelente oportunidade para focar sem interrupções em tarefas difíceis.",
                    type = AIInsightType.CALENDAR,
                    priority = InsightPriority.LOW,
                    action = null,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
        return list
    }
}
