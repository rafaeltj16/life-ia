package com.lifeos.core.ai.models

data class AIResponse(
    val text: String,
    val actions: List<AIAction> = emptyList(),
    val confidence: Float = 1.0f,
    val providerUsed: String = "LocalRules"
)
