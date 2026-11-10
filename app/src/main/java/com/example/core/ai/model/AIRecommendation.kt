package com.example.core.ai.model

data class AIRecommendation(
    val title: String,
    val description: String,
    val confidence: Float,
    val action: AIAction?
)
