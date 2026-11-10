package com.example.core.common

import java.util.UUID

enum class TaskStatus(val displayName: String) {
    TO_DO("A Fazer"),
    IN_PROGRESS("Em andamento"),
    WAITING("Aguardando"),
    COMPLETED("Concluída"),
    ARCHIVED("Arquivada")
}

data class SubTask(
    val id: String = UUID.randomUUID().toString(),
    val taskId: String,
    val title: String,
    val completed: Boolean = false
)

enum class TaskPriority {
    LOW, MEDIUM, HIGH
}

data class TaskItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val category: String,
    val isCompleted: Boolean = false,
    val dueDate: String,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = if (isCompleted) TaskStatus.COMPLETED else TaskStatus.TO_DO,
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val subTasks: List<SubTask> = emptyList(),
    val projectId: String? = null
) {
    val completed: Boolean get() = isCompleted

    fun withStatus(newStatus: TaskStatus): TaskItem {
        return this.copy(
            status = newStatus,
            isCompleted = (newStatus == TaskStatus.COMPLETED)
        )
    }

    fun withCompleted(completed: Boolean): TaskItem {
        return this.copy(
            isCompleted = completed,
            status = if (completed) TaskStatus.COMPLETED else TaskStatus.TO_DO
        )
    }

    fun withTags(newTags: List<String>): TaskItem {
        return this.copy(tags = newTags)
    }

    fun withFavorite(favorite: Boolean): TaskItem {
        return this.copy(isFavorite = favorite)
    }

    fun withSubTasks(newSubTasks: List<SubTask>): TaskItem {
        return this.copy(subTasks = newSubTasks)
    }
}

typealias Task = TaskItem
