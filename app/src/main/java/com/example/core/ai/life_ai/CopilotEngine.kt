package com.example.core.ai.life_ai

import com.example.core.ai.repository.AIMemoryItem

class CopilotEngine {
    private val geminiService = GeminiService()

    suspend fun generateResponseWithContext(
        query: String,
        memories: List<AIMemoryItem>
    ): String {
        val memoryContext = memories.joinToString("; ") { it.fact }
        val contextPrompt = if (memoryContext.isNotEmpty()) {
            """
                Informações e preferências persistentes do usuário (use isso para contextualizar e personalizar sua resposta):
                $memoryContext

                Mensagem do usuário:
                $query
            """.trimIndent()
        } else {
            query
        }
        return geminiService.generateResponse(contextPrompt)
    }
}
