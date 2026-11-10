package com.lifeos.core_ai.orchestrator

interface AICache {

    suspend fun get(
        key: String
    ): AIResponse?

    suspend fun save(
        key: String,
        response: AIResponse
    )

}
