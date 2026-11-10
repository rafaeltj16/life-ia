package com.lifeos.core_ai.learning

data class HabitRisk(
    val habitId: String,
    val probability: Float
)

class HabitRiskAnalyzer {

    fun analyze(
        completionRate: Float
    ): HabitRisk {
        val probability = 1f - completionRate
        return HabitRisk(
            habitId = "",
            probability = probability
        )
    }

}
