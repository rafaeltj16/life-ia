package com.lifeos.features.habits.domain.usecase

import com.example.core.common.HabitItem
import com.lifeos.features.habits.domain.repository.HabitRepository

class CompleteHabitUseCase(private val repository: HabitRepository) {
    suspend operator fun invoke(userId: String, habitId: String): HabitItem? {
        val habits = repository.getHabits(userId)
        val habit = habits.find { it.id == habitId } ?: return null
        
        val wasCompleted = habit.isCompletedToday
        val newStreak = if (!wasCompleted) habit.streak + 1 else maxOf(0, habit.streak - 1)
        val updatedHabit = habit.copy(
            isCompletedToday = !wasCompleted,
            streak = newStreak
        )
        repository.saveHabit(userId, updatedHabit)
        return updatedHabit
    }
}
