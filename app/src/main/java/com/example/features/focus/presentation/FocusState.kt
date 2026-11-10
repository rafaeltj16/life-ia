package com.example.features.focus.presentation

import com.example.features.focus.data.FocusSessionEntity

data class FocusState(
    val activeSession: FocusSessionEntity? = FocusSessionEntity(),
    val historySessions: List<FocusSessionEntity> = emptyList(),
    val isLoading: Boolean = false,
    val totalFocusMinutesToday: Int = 42,
    val focusScoreToday: Int = 92
)
