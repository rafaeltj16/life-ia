package com.example.features.focus.domain

class FocusScoreCalculator {

    fun calculateScore(
        durationMinutes: Int,
        elapsedSeconds: Int,
        interruptions: Int
    ): Int {
        if (elapsedSeconds <= 0) return 100
        val targetSeconds = durationMinutes * 60
        val completionRatio = (elapsedSeconds.toFloat() / targetSeconds.toFloat()).coerceAtMost(1.0f)
        
        val baseScore = (completionRatio * 100).toInt()
        val penalty = interruptions * 8
        
        return (baseScore - penalty).coerceIn(0, 100)
    }

    fun calculateEarnedXp(
        durationMinutes: Int,
        elapsedSeconds: Int,
        isFullyCompleted: Boolean
    ): Int {
        val minutesSpent = elapsedSeconds / 60
        var xp = minutesSpent * 2
        if (isFullyCompleted) {
            xp += 50
        }
        return xp
    }
}
