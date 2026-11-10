package com.lifeos.focus.domain.model

import java.time.Instant

data class FocusSession(
    val id: String,
    val taskId: String?,
    val projectId: String?,
    val startedAt: Instant,
    val endedAt: Instant? = null,
    val pauses: Int = 0,
    val interruptions: Int = 0,
    val completed: Boolean = false
)
