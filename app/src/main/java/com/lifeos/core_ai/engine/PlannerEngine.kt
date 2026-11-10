package com.lifeos.core_ai.engine

import com.example.core.ai.model.AIAction
import com.example.core.ai.model.DailyMission
import com.example.core.ai.model.InsightPriority

class PlannerEngine {
    fun generatePlan(missionTitle: String): DailyMission {
        return DailyMission(
            title = missionTitle,
            description = "Foco principal do dia agendado para 09:00 - 10:30",
            estimatedMinutes = 90,
            priority = InsightPriority.HIGH,
            action = AIAction.StartFocusSession(90)
        )
    }
}

