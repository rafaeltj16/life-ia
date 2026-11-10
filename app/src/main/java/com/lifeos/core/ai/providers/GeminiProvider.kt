package com.lifeos.core.ai.providers

import com.lifeos.core.ai.models.AIRequest
import com.lifeos.core.ai.models.AIResponse

class GeminiProvider {
    suspend fun generateResponse(request: AIRequest): AIResponse {
        return AIResponse(
            text = "Resposta gerada via Gemini: ${request.prompt}",
            providerUsed = "GeminiProvider"
        )
    }
}
