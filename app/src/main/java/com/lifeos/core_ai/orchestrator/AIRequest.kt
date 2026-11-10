package com.lifeos.core_ai.orchestrator

data class AIRequest(
    val type: PromptType,
    val prompt: String,
    val maxTokens: Int = 1024,
    val temperature: Float = 0.7f,
    val useCache: Boolean = true
)
