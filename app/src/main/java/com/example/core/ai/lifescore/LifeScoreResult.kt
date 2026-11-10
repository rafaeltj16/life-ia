package com.example.core.ai.lifescore

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LifeScoreResult(
    val overallScore: Int,
    val categoryScores: List<CategoryScore>,
    val weights: LifeScoreWeights = LifeScoreWeights.DEFAULT,
    val streakBonus: Int = 0,
    val insights: List<String> = emptyList(),
    val recommendations: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
) {
    val tier: ScoreTier
        get() = when {
            overallScore >= 90 -> ScoreTier.EXCELLENT
            overallScore >= 75 -> ScoreTier.GREAT
            overallScore >= 60 -> ScoreTier.GOOD
            overallScore >= 45 -> ScoreTier.MODERATE
            else -> ScoreTier.NEEDS_ATTENTION
        }

    val formattedDate: String
        get() = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))

    fun getCategoryScore(category: ScoreCategory): CategoryScore? {
        return categoryScores.find { it.category == category }
    }
}

enum class ScoreTier(
    val label: String,
    val description: String,
    val colorHex: Long,
    val badgeIcon: String
) {
    EXCELLENT("Excelente", "Desempenho extraordinário em todas as áreas!", 0xFF4CAF50, "🏆"),
    GREAT("Ótimo", "Ótima consistência e alto rendimento.", 0xFF8BC34A, "🌟"),
    GOOD("Bom", "Bom progresso. Continue mantendo a rotina.", 0xFF2196F3, "👍"),
    MODERATE("Moderado", "Algumas áreas precisam de ajuste de foco.", 0xFFFF9800, "⚠️"),
    NEEDS_ATTENTION("Atenção", "Priorize hábitos básicos e reduza tarefas pendentes.", 0xFFF44336, "🚩")
}
