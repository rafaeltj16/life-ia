package com.lifeos.focus.domain.usecase

import com.lifeos.focus.domain.model.FocusSession
import java.time.Instant

class StartFocusSessionUseCase {
    suspend operator fun invoke(taskId: String? = null, projectId: String? = null): FocusSession {
        return FocusSession(
            id = java.util.UUID.randomUUID().toString(),
            taskId = taskId,
            projectId = projectId,
            startedAt = Instant.now()
        )
    }
}
