package com.example.features.dashboard.data.remote

import com.example.core.network.SupabaseServiceClient

class SupabaseDashboardDataSource {
    suspend fun getDashboardMetrics(userId: String): SupabaseDashboardMetricsDto {
        return SupabaseServiceClient.getDashboardMetrics(userId)
    }

    suspend fun saveDashboardMetrics(metrics: SupabaseDashboardMetricsDto) {
        SupabaseServiceClient.saveDashboardMetrics(metrics)
    }
}
