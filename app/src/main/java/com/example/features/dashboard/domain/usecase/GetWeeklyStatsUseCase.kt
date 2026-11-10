package com.example.features.dashboard.domain.usecase

import com.lifeos.features.tasks.domain.repository.TaskRepository
import com.lifeos.features.habits.domain.repository.HabitRepository

class GetWeeklyStatsUseCase(
    private val taskRepository: TaskRepository,
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(userId: String): Map<String, Float> {
        val tasks = taskRepository.getTasks(userId)
        val habits = habitRepository.getHabits(userId)

        val completedTasks = tasks.count { it.isCompleted }
        val totalTasks = tasks.size
        val taskCompletionRate = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0.8f

        val completedHabits = habits.count { it.isCompletedToday }
        val totalHabits = habits.size
        val habitCompletionRate = if (totalHabits > 0) completedHabits.toFloat() / totalHabits else 0.7f

        val averageRate = (taskCompletionRate + habitCompletionRate) / 2f

        return mapOf(
            "Seg" to 0.8f,
            "Ter" to 0.7f,
            "Qua" to 0.9f,
            "Qui" to 0.6f,
            "Sex" to averageRate,
            "Sáb" to 0.5f,
            "Dom" to 0.4f
        )
    }
}
