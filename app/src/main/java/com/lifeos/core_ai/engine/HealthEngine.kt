package com.lifeos.core_ai.engine

import com.lifeos.core_ai.model.DashboardSnapshot

class HealthEngine {
    fun calculateHealthScore(snapshot: DashboardSnapshot): Int {
        val completedHabits = snapshot.habits.count { it.isCompletedToday }
        val totalHabits = snapshot.habits.size.coerceAtLeast(1)
        return ((completedHabits.toFloat() / totalHabits) * 100).toInt().coerceIn(0, 100)
    }
}
