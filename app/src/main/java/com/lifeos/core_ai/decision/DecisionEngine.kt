package com.lifeos.core_ai.decision

import com.lifeos.core_ai.model.DashboardSnapshot
import com.lifeos.tasks.domain.model.isOverdue

class DecisionEngine {

    fun evaluate(
        snapshot: DashboardSnapshot
    ): Decision {
        val overdue = snapshot.tasks.count {
            it.isOverdue
        }
        if (overdue >= 5) {
            return Decision(
                type = DecisionType.GEMINI,
                reason = DecisionReason.MANY_OVERDUE_TASKS,
                confidence = 0.95f
            )
        }
        if (snapshot.projects.size >= 8) {
            return Decision(
                type = DecisionType.GEMINI,
                reason = DecisionReason.COMPLEX_ANALYSIS,
                confidence = 0.90f
            )
        }
        return Decision(
            type = DecisionType.LOCAL,
            reason = DecisionReason.SIMPLE_UPDATE,
            confidence = 0.99f
        )
    }
}
