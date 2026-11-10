package com.lifeos.core.planner

data class PriorityScore(
    val urgency: Int,
    val impact: Int,
    val deadlineWeight: Int,
    val goalWeight: Int,
    val energyMatch: Int
) {
    val prioritySum: Int
        get() = urgency + impact + goalWeight + deadlineWeight + energyMatch

    fun calculateTotalScore(): Float {
        return (prioritySum / 50.0f).coerceIn(0.0f, 1.0f)
    }
}

