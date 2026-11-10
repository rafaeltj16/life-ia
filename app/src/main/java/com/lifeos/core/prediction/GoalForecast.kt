package com.lifeos.core.prediction

data class GoalForecast(
    val goalId: String,
    val goalName: String,
    val targetCompletionDays: Int,
    val probabilityOfOnTimeCompletion: Float, // 0.0 - 1.0
    val predictedDelayDays: Int = 0,
    val recommendedAdjustments: List<String> = emptyList()
)
