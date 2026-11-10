package com.lifeos.core_ai.orchestrator

import com.lifeos.core_ai.model.DashboardSnapshot

class PromptBuilder {

    fun buildDailyPlan(
        snapshot: DashboardSnapshot
    ): String {

        return """
Você é o LifeAI.

Analise os dados abaixo.

Tarefas:
${snapshot.tasks.size}

Hábitos:
${snapshot.habits.size}

Projetos:
${snapshot.projects.size}

Eventos:
${snapshot.events.size}

Retorne:

- missão principal

- três recomendações

- riscos

- melhor horário para foco

Resposta em português.
""".trimIndent()

    }

}
