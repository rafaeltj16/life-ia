package com.example.features.dashboard.domain.usecase

import com.example.features.dashboard.domain.model.DashboardData
import com.example.features.dashboard.domain.repository.DashboardRepository

class GetDashboardDataUseCase(
    private val dashboardRepository: DashboardRepository
) {
    suspend operator fun invoke(userId: String = ""): DashboardData {
        return dashboardRepository.getDashboard()
    }
}
