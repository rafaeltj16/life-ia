package com.lifeos.core.ai.providers

import com.lifeos.core.ai.models.AIAction
import com.lifeos.core.ai.models.AIRequest
import com.lifeos.core.ai.models.AIResponse

class LocalRulesProvider {
    fun generateResponse(request: AIRequest): AIResponse {
        val query = request.prompt.lowercase()
        return when {
            query.contains("tarefa") || query.contains("task") -> {
                AIResponse(
                    text = "Aqui está uma recomendação de tarefa baseada em suas regras locais.",
                    actions = listOf(AIAction.CreateTask("Revisar metas do dia", "Produtividade")),
                    providerUsed = "LocalRules"
                )
            }
            query.contains("foco") || query.contains("focus") -> {
                AIResponse(
                    text = "Iniciando modo de foco.",
                    actions = listOf(AIAction.StartFocusSession(25)),
                    providerUsed = "LocalRules"
                )
            }
            else -> {
                AIResponse(
                    text = "Processado via regras locais.",
                    actions = emptyList(),
                    providerUsed = "LocalRules"
                )
            }
        }
    }
}
