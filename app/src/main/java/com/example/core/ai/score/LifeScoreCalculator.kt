package com.example.core.ai.score

import com.example.core.ai.model.DashboardData

class LifeScoreCalculator {

    fun calculate(
        data: DashboardData
    ): Int {

        var score = 100

        score -= data.overdueTasks * 5

        score += data.currentStreak

        score += data.completedHabitsToday * 2

        score -= data.inactiveProjects * 4

        return score.coerceIn(0, 100)

    }

}
