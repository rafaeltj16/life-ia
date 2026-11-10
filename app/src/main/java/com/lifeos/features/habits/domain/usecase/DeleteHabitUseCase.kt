package com.lifeos.features.habits.domain.usecase

import com.lifeos.features.habits.domain.repository.HabitRepository

class DeleteHabitUseCase(private val repository: HabitRepository) {
    suspend operator fun invoke(userId: String, habitId: String) {
        repository.deleteHabit(userId, habitId)
    }
}
