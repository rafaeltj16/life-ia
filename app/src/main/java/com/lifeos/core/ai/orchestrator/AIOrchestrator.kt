package com.lifeos.core.ai.orchestrator

import com.lifeos.core.ai.models.AIRequest
import com.lifeos.core.ai.models.AIResponse
import com.lifeos.core.ai.providers.GeminiProvider
import com.lifeos.core.ai.providers.LocalRulesProvider
import com.lifeos.core.ai.providers.OfflineProvider

class AIOrchestrator(
    private val geminiProvider: GeminiProvider = GeminiProvider(),
    private val localRulesProvider: LocalRulesProvider = LocalRulesProvider(),
    private val offlineProvider: OfflineProvider = OfflineProvider(),
    private val intentDetector: AIIntentDetector = AIIntentDetector(),
    private val actionExecutor: AIActionExecutor = AIActionExecutor(),
    private val historyManager: AIHistoryManager = AIHistoryManager(),
    private val responseFormatter: AIResponseFormatter = AIResponseFormatter(),
    private val promptBuilder: PromptBuilder = PromptBuilder()
) {
    suspend fun processRequest(request: AIRequest): AIResponse {
        val intent = intentDetector.detectIntent(request.prompt)
        
        val response = if (request.useOnlineProvider) {
            try {
                geminiProvider.generateResponse(request)
            } catch (e: Exception) {
                localRulesProvider.generateResponse(request)
            }
        } else {
            localRulesProvider.generateResponse(request)
        }

        if (response.actions.isNotEmpty()) {
            actionExecutor.executeAll(response.actions)
        }

        historyManager.recordInteraction(request, response)
        return response
    }
}
