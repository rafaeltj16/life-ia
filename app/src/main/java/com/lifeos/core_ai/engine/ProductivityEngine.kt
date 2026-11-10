package com.lifeos.core_ai.engine

import com.lifeos.core_ai.model.DashboardSnapshot

class ProductivityEngine {
    fun calculateProductivityIndex(snapshot: DashboardSnapshot): Int {
        val tasksCompleted = snapshot.tasks.count { it.isCompleted }
        val habitsCompleted = snapshot.habits.count { it.isCompletedToday }
        return ((tasksCompleted * 10) + (habitsCompleted * 15)).coerceAtMost(100)
    }
}
