package com.example.features.dashboard.domain.repository

import com.example.features.dashboard.domain.model.DashboardData

interface DashboardRepository {
    suspend fun getDashboard(): DashboardData
}
