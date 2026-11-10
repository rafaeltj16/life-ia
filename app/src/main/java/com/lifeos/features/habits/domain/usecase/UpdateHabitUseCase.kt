package com.lifeos.features.habits.domain.usecase

import com.example.core.common.HabitItem
import com.lifeos.features.habits.domain.repository.HabitRepository

class UpdateHabitUseCase(private val repository: HabitRepository) {
    suspend operator fun invoke(userId: String, habit: HabitItem) {
        repository.saveHabit(userId, habit)
    }
}
