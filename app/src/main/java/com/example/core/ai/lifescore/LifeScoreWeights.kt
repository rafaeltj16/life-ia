package com.example.core.ai.lifescore

data class LifeScoreWeights(
    val productivity: Float = 0.25f,
    val habits: Float = 0.20f,
    val health: Float = 0.15f,
    val projects: Float = 0.15f,
    val finance: Float = 0.10f,
    val focus: Float = 0.15f
) {
    val taskWeight: Float get() = productivity
    val habitWeight: Float get() = habits
    val projectWeight: Float get() = projects
    val financeWeight: Float get() = finance
    val healthWeight: Float get() = health
    val focusWeight: Float get() = focus

    fun getWeightFor(category: ScoreCategory): Float {
        return when (category) {
            ScoreCategory.PRODUCTIVITY -> productivity
            ScoreCategory.HABITS -> habits
            ScoreCategory.HEALTH -> health
            ScoreCategory.PROJECTS -> projects
            ScoreCategory.FINANCE -> finance
            ScoreCategory.FOCUS -> focus
        }
    }

    val totalWeight: Float get() = productivity + habits + health + projects + finance + focus

    fun normalizedWeights(): LifeScoreWeights {
        val total = totalWeight
        if (total <= 0f) return LifeScoreWeights()
        return LifeScoreWeights(
            productivity = productivity / total,
            habits = habits / total,
            health = health / total,
            projects = projects / total,
            finance = finance / total,
            focus = focus / total
        )
    }

    companion object {
        val DEFAULT = LifeScoreWeights()
        val BALANCED = LifeScoreWeights(0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.20f)
        val HABIT_FOCUSED = LifeScoreWeights(0.15f, 0.40f, 0.15f, 0.15f, 0.05f, 0.10f)
        val PRODUCTIVITY_FOCUSED = LifeScoreWeights(0.35f, 0.20f, 0.10f, 0.20f, 0.05f, 0.10f)
    }
}
