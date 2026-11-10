package com.example.features.copilot.domain

import com.example.features.copilot.data.DailyMission
import com.example.features.copilot.data.WeeklyReview

class CopilotProgressEngine {
    
    fun calculateDailyProgress(missions: List<DailyMission>): Float {
        if (missions.isEmpty()) return 0f
        val completed = missions.count { it.isCompleted }
        return completed.toFloat() / missions.size.toFloat()
    }

    fun calculateTotalXP(missions: List<DailyMission>): Int {
        return missions.filter { it.isCompleted }.sumOf { it.points }
    }

    fun calculateProductivityGrade(review: WeeklyReview?): String {
        val score = review?.productivityScore ?: 0
        return when {
            score >= 90 -> "S (Excepcional)"
            score >= 80 -> "A (Excelente)"
            score >= 70 -> "B (Bom ritmo)"
            score >= 60 -> "C (Satisfatório)"
            else -> "D (Atenção necessária)"
        }
    }
}
