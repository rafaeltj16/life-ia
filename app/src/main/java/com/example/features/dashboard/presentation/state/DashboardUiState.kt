package com.example.features.dashboard.presentation.state

import com.example.features.dashboard.domain.model.DashboardData

sealed interface DashboardUiState {

    data object Loading : DashboardUiState

    data class Success(
        val dashboard: DashboardData
    ) : DashboardUiState

    data class Error(
        val message: String
    ) : DashboardUiState

}
