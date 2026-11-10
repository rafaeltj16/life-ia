package com.lifeos.features.habits.domain.usecase

import com.example.core.common.HabitItem
import com.lifeos.features.habits.domain.repository.HabitRepository
import java.util.UUID

class CreateHabitUseCase(private val repository: HabitRepository) {
    suspend operator fun invoke(userId: String, habit: HabitItem): HabitItem {
        repository.saveHabit(userId, habit)
        return habit
    }

    suspend operator fun invoke(userId: String, name: String, icon: String = "⚡"): HabitItem {
        val habit = HabitItem(
            id = UUID.randomUUID().toString(),
            name = name,
            streak = 0,
            isCompletedToday = false,
            icon = icon.ifEmpty { "⚡" }
        )
        repository.saveHabit(userId, habit)
        return habit
    }
}
