package com.example.features.dashboard.domain.usecase

import com.example.core.common.TaskItem
import com.lifeos.features.tasks.domain.repository.TaskRepository

class GetTodayTasksUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(userId: String): List<TaskItem> {
        return taskRepository.getTasks(userId)
    }
}
