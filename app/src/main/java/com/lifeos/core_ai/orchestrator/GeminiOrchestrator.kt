package com.lifeos.core_ai.orchestrator

import com.lifeos.core_ai.model.DashboardSnapshot

interface GeminiClient {
    suspend fun generate(
        request: AIRequest
    ): AIResponse
}

class GeminiOrchestrator(
    private val client: GeminiClient,
    private val promptBuilder: PromptBuilder,
    private val config: AIConfig
) {

    suspend fun generateDailyPlan(
        snapshot: DashboardSnapshot
    ): AIResponse {
        val prompt =
            promptBuilder.buildDailyPlan(snapshot)

        val request = AIRequest(
            type = PromptType.DAILY_PLAN,
            prompt = prompt,
            maxTokens = 1200,
            temperature = 0.4f
        )

        return client.generate(request)
    }
}
