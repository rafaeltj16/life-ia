package com.lifeos.core.prediction

data class ProductivityModel(
    val focusScore: Float = 0.85f,
    val taskVelocity: Float = 3.2f, // tasks per day
    val peakFocusHour: Int = 10, // 10 AM
    val burnoutRisk: Float = 0.15f
)
