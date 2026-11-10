package com.example.features.ai.data.remote

import com.example.core.network.GeminiServiceClient

class GeminiDataSource {
    suspend fun generateResponse(prompt: String): String {
        return GeminiServiceClient.generateResponse(prompt)
    }
}
