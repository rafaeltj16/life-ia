package com.lifeos.focus.domain.usecase

import com.lifeos.focus.domain.model.FocusSession
import java.time.Instant

class FinishFocusSessionUseCase {
    suspend operator fun invoke(session: FocusSession? = null): FocusSession? {
        return session?.copy(
            endedAt = Instant.now(),
            completed = true
        )
    }
}
