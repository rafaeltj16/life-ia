package com.example.features.dashboard.domain.usecase

import com.example.core.common.HabitItem
import com.lifeos.features.habits.domain.repository.HabitRepository

data class HabitSummary(
    val habits: List<HabitItem>,
    val totalCount: Int,
    val completedCount: Int,
    val completionRate: Float,
    val bestStreak: Int
)

class GetHabitSummaryUseCase(private val habitRepository: HabitRepository) {
    suspend operator fun invoke(userId: String): HabitSummary {
        val habits = habitRepository.getHabits(userId)
        val completed = habits.count { it.isCompletedToday }
        val total = habits.size
        val rate = if (total > 0) completed.toFloat() / total else 0f
        val bestStreak = habits.maxOfOrNull { it.streak } ?: 0
        return HabitSummary(
            habits = habits,
            totalCount = total,
            completedCount = completed,
            completionRate = rate,
            bestStreak = bestStreak
        )
    }
}
