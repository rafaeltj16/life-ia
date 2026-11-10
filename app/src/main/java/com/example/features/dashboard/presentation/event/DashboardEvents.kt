package com.example.features.dashboard.presentation.event

sealed interface DashboardEvent {
    data object Refresh : DashboardEvent
    data object Retry : DashboardEvent
    data class OpenTask(val id: String) : DashboardEvent
    data class OpenHabit(val id: String) : DashboardEvent
    data class CompleteHabit(val id: String) : DashboardEvent
}
