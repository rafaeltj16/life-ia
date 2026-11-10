package com.example.core.ai.prediction

import com.example.core.ai.memory.MemoryProfile
import com.example.core.ai.model.DashboardData

class PredictionEngine {

    fun predictBestFocusHour(
        profile: MemoryProfile
    ): Int? {
        return profile.bestFocusTime?.hour
    }

    fun predictSuccessRate(
        dashboard: DashboardData
    ): Int {

        var score = 100

        score -= dashboard.overdueTasks * 8
        score -= dashboard.inactiveProjects * 5

        if (dashboard.waterPercentage < 60)
            score -= 10

        return score.coerceIn(0, 100)
    }
}
