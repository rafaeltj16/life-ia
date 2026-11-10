package com.example.features.dashboard.data.repository

import com.example.features.dashboard.domain.model.DashboardData
import com.example.features.dashboard.domain.model.AIInsight
import com.example.core.common.DashboardMetricsCalculator
import com.example.core.common.UserDashboardMetrics
import com.example.features.dashboard.data.local.DashboardDao
import com.example.features.dashboard.data.mapper.DashboardMapper
import com.example.features.dashboard.data.remote.SupabaseDashboardDataSource
import com.example.features.dashboard.domain.repository.DashboardRepository
import com.lifeos.features.tasks.domain.repository.TaskRepository
import com.lifeos.features.habits.domain.repository.HabitRepository
import com.example.features.projects.domain.repository.ProjectRepository
import com.example.features.finance.domain.repository.FinanceRepository

class DashboardRepositoryImpl(
    private val localDataSource: DashboardDao,
    private val remoteDataSource: SupabaseDashboardDataSource,
    private val taskRepository: TaskRepository,
    private val habitRepository: HabitRepository,
    private val projectRepository: ProjectRepository,
    private val financeRepository: FinanceRepository,
    private val getCurrentUserId: () -> String = { "mock-uuid-1234" }
) : DashboardRepository {

    override suspend fun getDashboard(): DashboardData {
        val userId = getCurrentUserId()
        
        // 1. Fetch metrics from DB or remote
        val metrics = getDashboardMetrics(userId)
        
        // 2. Fetch other sections from their respective repositories
        val tasks = taskRepository.getTasks(userId)
        val habits = habitRepository.getHabits(userId)
        val projects = projectRepository.getProjects(userId)
        val transactions = financeRepository.getTransactions(userId)
        
        // 3. Compile metrics and build domain object DashboardData
        val completedTasks = tasks.count { it.isCompleted }
        val totalTasks = tasks.size
        val taskCompletionRate = DashboardMetricsCalculator.calculateTaskCompletionRate(completedTasks, totalTasks)

        val completedHabits = habits.count { it.isCompletedToday }
        val totalHabits = habits.size
        val habitCompletionRate = DashboardMetricsCalculator.calculateHabitCompletionRate(completedHabits, totalHabits)

        val averageRate = DashboardMetricsCalculator.calculateAverageRate(taskCompletionRate, habitCompletionRate)
        val streak = metrics.habitStreak
        val xp = DashboardMetricsCalculator.calculateXP(
            completedTasksCount = completedTasks,
            completedHabitsCount = completedHabits,
            streakDays = streak
        )
        val level = DashboardMetricsCalculator.calculateLevel(xp)

        val user = com.example.core.common.User(
            id = userId,
            email = "rafaeltj16@gmail.com",
            name = "Rafael",
            level = level,
            xp = xp,
            streak = streak
        )

        val weeklyStats = DashboardMetricsCalculator.calculateWeeklyStats(
            completedTasks = completedTasks,
            completedHabits = completedHabits,
            totalTasks = totalTasks,
            totalHabits = totalHabits,
            averageRate = averageRate
        )

        val financeSummary = DashboardMetricsCalculator.calculateFinanceSummary(transactions)

        val aiInsight = AIInsight(
            summary = metrics.lastAiSummary.ifBlank { "Mantenha o foco em suas metas diárias!" },
            recommendations = listOf(
                metrics.lastAiSummary.ifBlank { "Mantenha o foco em suas metas diárias!" },
                "Beba água para manter a saúde e energia em alta."
            )
        )

        return DashboardData(
            user = user,
            xp = xp,
            level = level,
            streak = streak,
            todayHabits = habits,
            todayTasks = tasks,
            projects = projects,
            events = emptyList(),
            weeklyStats = weeklyStats,
            financeSummary = financeSummary,
            aiInsight = aiInsight
        )
    }

    private suspend fun getDashboardMetrics(userId: String): UserDashboardMetrics {
        return try {
            val remoteMetricsDto = remoteDataSource.getDashboardMetrics(userId)
            val remoteMetrics = DashboardMapper.toDomain(remoteMetricsDto)
            localDataSource.insertOrUpdateMetrics(DashboardMapper.toEntity(remoteMetrics))
            remoteMetrics
        } catch (e: Exception) {
            val cachedEntity = localDataSource.getMetrics(userId)
            if (cachedEntity != null) {
                DashboardMapper.toDomain(cachedEntity)
            } else {
                val fallbackDto = remoteDataSource.getDashboardMetrics(userId)
                val fallback = DashboardMapper.toDomain(fallbackDto)
                localDataSource.insertOrUpdateMetrics(DashboardMapper.toEntity(fallback))
                fallback
            }
        }
    }

    suspend fun saveDashboardMetrics(metrics: UserDashboardMetrics) {
        localDataSource.insertOrUpdateMetrics(DashboardMapper.toEntity(metrics))
        try {
            val dto = DashboardMapper.toDto(metrics)
            remoteDataSource.saveDashboardMetrics(dto)
        } catch (e: Exception) {
            // Keep local changes even if remote sync fails temporarily
        }
    }
}
