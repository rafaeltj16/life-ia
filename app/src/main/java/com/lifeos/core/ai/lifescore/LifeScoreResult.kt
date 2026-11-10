package com.lifeos.core.ai.lifescore

data class LifeScoreResult(
    val overall: Int,
    val productivity: Int,
    val habits: Int,
    val health: Int,
    val projects: Int,
    val finance: Int,
    val focus: Int
)
