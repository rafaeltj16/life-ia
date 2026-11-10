package com.lifeos.features.habits.domain.usecase

import com.example.core.common.HabitItem
import com.lifeos.features.habits.domain.repository.HabitRepository

class GetHabitsUseCase(private val repository: HabitRepository) {
    suspend operator fun invoke(userId: String): List<HabitItem> {
        return repository.getHabits(userId)
    }
}
