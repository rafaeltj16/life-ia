package com.lifeos.core_ai.notification

import com.lifeos.core_ai.provider.TaskProvider
import java.time.LocalDateTime

class OverdueTaskRule(
    private val taskProvider: TaskProvider
) : NotificationRule {

    override suspend fun shouldNotify(): Boolean {
        return taskProvider
            .getOverdueTasks()
            .isNotEmpty()
    }

    override suspend fun build(): SmartNotification {
        return SmartNotification(
            id = "tasks",
            title = "Existem tarefas atrasadas",
            message = "Resolva primeiro as tarefas críticas.",
            priority = NotificationPriority.CRITICAL,
            scheduledAt = LocalDateTime.now()
        )
    }

}
