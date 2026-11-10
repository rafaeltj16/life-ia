package com.lifeos.core.ai.models

data class AIRequest(
    val prompt: String,
    val context: AIContext = AIContext(),
    val useOnlineProvider: Boolean = true
)
