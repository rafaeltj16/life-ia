package com.lifeos.core.ai.lifescore

data class LifeScoreWeights(
    val productivity: Float = 0.25f,
    val habits: Float = 0.20f,
    val health: Float = 0.15f,
    val projects: Float = 0.15f,
    val finance: Float = 0.10f,
    val focus: Float = 0.15f
)
