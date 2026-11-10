package com.lifeos.core.ai.lifescore

import kotlin.math.roundToInt

class LifeScoreEngine(
    private val weights: LifeScoreWeights = LifeScoreWeights()
) {
    fun calculate(
        productivity: Int,
        habits: Int,
        health: Int,
        projects: Int,
        finance: Int,
        focus: Int
    ): LifeScoreResult {
        val overall = (
            productivity * weights.productivity +
            habits * weights.habits +
            health * weights.health +
            projects * weights.projects +
            finance * weights.finance +
            focus * weights.focus
        ).roundToInt()

        return LifeScoreResult(
            overall = overall,
            productivity = productivity,
            habits = habits,
            health = health,
            projects = projects,
            finance = finance,
            focus = focus
        )
    }
}
