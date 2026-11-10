package com.lifeos.core_ai.orchestrator

data class AIResponse(
    val success: Boolean,
    val text: String,
    val model: AIModel,
    val tokensUsed: Int,
    val cached: Boolean,
    val executionTime: Long
)
