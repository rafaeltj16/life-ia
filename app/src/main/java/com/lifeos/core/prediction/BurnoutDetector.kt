package com.lifeos.core.prediction

class BurnoutDetector {

    fun calculateBurnoutRisk(
        weeklyFocusHours: Float,
        lateNightWorkSessions: Int,
        habitStreakConsistency: Float
    ): Float {
        var risk = 0.1f

        if (weeklyFocusHours > 40f) risk += 0.3f
        if (lateNightWorkSessions > 3) risk += 0.35f
        if (habitStreakConsistency < 0.5f) risk += 0.2f

        return risk.coerceIn(0.0f, 1.0f)
    }

    fun getBurnoutAlertMessage(riskLevel: Float): String? {
        return when {
            riskLevel > 0.7f -> "⚠️ Risco elevado de Burnout! Recomendado reduzir o ritmo e agendar pausas na agenda."
            riskLevel > 0.4f -> "⚡ Atenção com o ritmo de trabalho. Mantenha os horários de sono regulares."
            else -> null
        }
    }
}
