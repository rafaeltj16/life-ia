package com.lifeos.core_ai.engine

import com.lifeos.core_ai.model.DashboardSnapshot

class GoalEngine {
    fun evaluateProgress(snapshot: DashboardSnapshot): Int {
        val completedGoals = snapshot.tasks.count { it.isCompleted }
        val total = snapshot.tasks.size.coerceAtLeast(1)
        return ((completedGoals.toFloat() / total) * 100).toInt()
    }
}
