package com.lifeos.core.ai.providers

import com.lifeos.core.ai.models.AIAction
import com.lifeos.core.ai.models.AIRequest
import com.lifeos.core.ai.models.AIResponse

class OfflineProvider {
    fun generateResponse(request: AIRequest): AIResponse {
        return AIResponse(
            text = "Modo Offline: Não há conexão com a internet.",
            actions = listOf(AIAction.ShowMessage("Modo offline ativo")),
            providerUsed = "OfflineProvider"
        )
    }
}
