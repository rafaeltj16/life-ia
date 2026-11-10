package com.example.features.dashboard.data.mapper

import com.example.core.common.UserDashboardMetrics
import com.example.features.dashboard.data.local.DashboardMetricsEntity
import com.example.features.dashboard.data.remote.SupabaseDashboardMetricsDto

object DashboardMapper {
    fun toEntity(domain: UserDashboardMetrics): DashboardMetricsEntity {
        return DashboardMetricsEntity(
            userId = domain.userId,
            tasksToday = domain.tasksToday,
            completedToday = domain.completedToday,
            habitStreak = domain.habitStreak,
            weeklyGoal = domain.weeklyGoal,
            pendingProjects = domain.pendingProjects,
            lastAiSummary = domain.lastAiSummary,
            updatedAt = domain.updatedAt
        )
    }

    fun toDomain(entity: DashboardMetricsEntity): UserDashboardMetrics {
        return UserDashboardMetrics(
            userId = entity.userId,
            tasksToday = entity.tasksToday,
            completedToday = entity.completedToday,
            habitStreak = entity.habitStreak,
            weeklyGoal = entity.weeklyGoal,
            pendingProjects = entity.pendingProjects,
            lastAiSummary = entity.lastAiSummary,
            updatedAt = entity.updatedAt
        )
    }

    fun toDomain(dto: SupabaseDashboardMetricsDto): UserDashboardMetrics {
        return UserDashboardMetrics(
            userId = dto.userId,
            tasksToday = dto.tasksToday,
            completedToday = dto.completedToday,
            habitStreak = dto.habitStreak,
            weeklyGoal = dto.weeklyGoal,
            pendingProjects = dto.pendingProjects,
            lastAiSummary = dto.lastAiSummary,
            updatedAt = dto.updatedAt
        )
    }

    fun toDto(domain: UserDashboardMetrics): SupabaseDashboardMetricsDto {
        return SupabaseDashboardMetricsDto(
            userId = domain.userId,
            tasksToday = domain.tasksToday,
            completedToday = domain.completedToday,
            habitStreak = domain.habitStreak,
            weeklyGoal = domain.weeklyGoal,
            pendingProjects = domain.pendingProjects,
            lastAiSummary = domain.lastAiSummary,
            updatedAt = domain.updatedAt
        )
    }
}

