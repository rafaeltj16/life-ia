package com.lifeos.core_ai.orchestrator

data class AIConfig(
    val defaultModel: AIModel = AIModel.GEMINI_FLASH,
    val defaultTemperature: Float = 0.7f,
    val defaultMaxTokens: Int = 1024
)
