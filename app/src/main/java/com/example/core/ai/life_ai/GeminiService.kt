package com.example.core.ai.life_ai

import com.example.core.network.GeminiServiceClient

class GeminiService {
    suspend fun generateResponse(prompt: String): String {
        return GeminiServiceClient.generateResponse(prompt)
    }
}
