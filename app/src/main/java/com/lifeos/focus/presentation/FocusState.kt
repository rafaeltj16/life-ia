package com.lifeos.focus.presentation

import com.lifeos.focus.domain.model.FocusSession

data class FocusState(
    val running: Boolean = false,
    val paused: Boolean = false,
    val remainingSeconds: Long = 1500,
    val currentSession: FocusSession? = null,
    val todayMinutes: Int = 0,
    val streak: Int = 0
)
