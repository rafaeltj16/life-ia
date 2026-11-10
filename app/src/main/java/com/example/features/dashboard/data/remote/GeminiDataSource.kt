package com.example.features.dashboard.data.remote

import com.example.core.network.GeminiServiceClient

class GeminiDataSource {
    suspend fun generateDashboardSuggestions(prompt: String): String {
        return GeminiServiceClient.generateResponse(prompt)
    }
}
