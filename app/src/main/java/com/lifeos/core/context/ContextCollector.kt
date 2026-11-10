package com.lifeos.core.context

import com.example.core.common.Task
import java.time.Instant

class ContextCollector {
    fun collectCurrentSnapshot(activeTask: Task? = null): ContextSnapshot {
        return ContextSnapshot(
            timestamp = Instant.now(),
            currentTask = activeTask ?: Task(id = "task_ctx", title = "Implementar Context Engine", category = "Trabalho", dueDate = "Hoje", isCompleted = false),
            energy = 8,
            lifeScore = 92,
            mood = Mood.GREAT
        )
    }
}

