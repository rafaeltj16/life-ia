package com.lifeos.core.planner

class PriorityCalculator {
    fun calculateTaskPriority(
        isUrgent: Boolean,
        isImportant: Boolean,
        dueDateDaysRemaining: Int,
        energyLevelRequired: Int = 3
    ): Float {
        var score = 0.0f
        if (isImportant) score += 0.4f
        if (isUrgent) score += 0.3f

        val urgencyFactor = when {
            dueDateDaysRemaining <= 1 -> 0.2f
            dueDateDaysRemaining <= 3 -> 0.1f
            else -> 0.05f
        }
        score += urgencyFactor

        val energyBonus = (energyLevelRequired / 5.0f) * 0.1f
        score += energyBonus

        return score.coerceIn(0.0f, 1.0f)
    }

    fun computePriorityScore(
        urgency: Int,
        impact: Int,
        deadlineWeight: Int,
        goalWeight: Int,
        energyMatch: Int
    ): PriorityScore {
        return PriorityScore(
            urgency = urgency.coerceIn(1, 10),
            impact = impact.coerceIn(1, 10),
            deadlineWeight = deadlineWeight.coerceIn(1, 10),
            goalWeight = goalWeight.coerceIn(1, 10),
            energyMatch = energyMatch.coerceIn(1, 10)
        )
    }
}

