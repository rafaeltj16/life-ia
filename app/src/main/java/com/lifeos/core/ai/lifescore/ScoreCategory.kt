package com.lifeos.core.ai.lifescore

enum class ScoreCategory(
    val displayName: String = "",
    val icon: String = "",
    val description: String = "",
    val defaultWeight: Float = 0.16f
) {
    PRODUCTIVITY("Produtividade & Tarefas", "📋", "Conclusão e pontualidade na execução de tarefas", 0.20f),
    HABITS("Hábitos & Rituais", "⚡", "Consistência na manutenção de hábitos diários", 0.20f),
    HEALTH("Saúde & Bem-Estar", "🧘", "Exercícios, sono, hidratação e bem-estar", 0.15f),
    PROJECTS("Projetos & Metas", "🎯", "Progresso nos projetos e objetivos de longo prazo", 0.15f),
    FINANCE("Saúde Financeira", "💰", "Controle financeiro, orçamentos e economia", 0.15f),
    FOCUS("Foco & Concentração", "🧠", "Sessões de foco profundo e gerenciamento do tempo", 0.15f)
}

data class CategoryScore(
    val category: ScoreCategory,
    val score: Int,
    val maxScore: Int = 100,
    val details: String = ""
) {
    val percentage: Float get() = if (maxScore > 0) (score.toFloat() / maxScore.toFloat()).coerceIn(0f, 1f) else 0f
}
