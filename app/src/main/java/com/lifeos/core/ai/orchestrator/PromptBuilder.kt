package com.lifeos.core.ai.orchestrator

import com.lifeos.core.ai.models.AIContext

class PromptBuilder {
    fun buildSystemPrompt(context: AIContext): String {
        return """
            Você é o assistente inteligente LifeOS.
            Contexto do Usuário:
            - Hora Atual: ${context.currentTime}
            - Life Score: ${context.lifeScore}
            - Score de Produtividade: ${context.productivityScore}
            - Meta Ativa: ${context.activeGoal?.title ?: "Nenhuma"}
            - Tarefas Hoje: ${context.todayTasks.size} (${context.todayTasks.count { it.isCompleted }} concluídas)
            - Hábitos Hoje: ${context.todayHabits.size} (${context.todayHabits.count { it.isCompletedToday }} concluídos)
            - Eventos na Agenda: ${context.calendarEvents.size}
            - Sessão de Foco Ativa: ${if (context.activeFocusSession != null) "Sim" else "Não"}
            
            Responda de forma objetiva, acionável e motivadora.
        """.trimIndent()
    }
}
