package com.example.features.dashboard.domain.model

import com.example.core.common.User
import com.example.core.common.Habit
import com.example.core.common.Task
import com.example.core.common.Project
import com.example.core.common.CalendarEvent
import com.example.core.common.Transaction
import com.example.core.ai.model.DailyPlan

data class WeeklyStats(
    val rates: Map<String, Float> = emptyMap(),
    val completedCount: Int = 0,
    val totalCount: Int = 0
)

data class FinanceSummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList()
)

data class AIInsight(
    val summary: String = "",
    val recommendations: List<String> = emptyList()
)

data class DashboardData(
    val user: User = User(id = "", email = "", name = ""),
    val xp: Int = 0,
    val level: Int = 1,
    val streak: Int = 0,
    val todayHabits: List<Habit> = emptyList(),
    val todayTasks: List<Task> = emptyList(),
    val projects: List<Project> = emptyList(),
    val events: List<CalendarEvent> = emptyList(),
    val weeklyStats: WeeklyStats = WeeklyStats(),
    val financeSummary: FinanceSummary = FinanceSummary(),
    val aiInsight: AIInsight = AIInsight(),
    val dailyPlan: DailyPlan? = null
)
