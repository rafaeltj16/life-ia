package com.lifeos.core.prediction

class HabitRiskAnalyzer {

    fun analyzeRisk(habitName: String, missedDaysInRow: Int): Pair<Float, String> {
        val riskScore = (missedDaysInRow * 0.25f).coerceIn(0.0f, 1.0f)
        val warning = when {
            missedDaysInRow >= 3 -> "Risco Alto: O hábito '$habitName' está prestes a ser quebrado."
            missedDaysInRow >= 1 -> "Atenção: Complete '$habitName' hoje para manter a sequência."
            else -> "Hábito '$habitName' em dia!"
        }
        return riskScore to warning
    }
}
