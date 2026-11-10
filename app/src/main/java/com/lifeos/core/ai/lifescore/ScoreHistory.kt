package com.lifeos.core.ai.lifescore

import java.util.UUID

data class ScoreHistory(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val result: LifeScoreResult
)
