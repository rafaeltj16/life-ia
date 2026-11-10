package com.example.core.common

import com.example.features.dashboard.domain.model.WeeklyStats
import com.example.features.dashboard.domain.model.FinanceSummary

object DashboardMetricsCalculator {

    fun calculateXP(
        completedTasksCount: Int,
        completedHabitsCount: Int,
        completedProjectsCount: Int = 0,
        streakDays: Int = 0,
        completedDailyMission: Boolean = false,
        baseXP: Int = 2500
    ): Int {
        var xp = baseXP
        xp += completedHabitsCount * 10
        xp += completedTasksCount * 25
        xp += completedProjectsCount * 100
        if (streakDays >= 7) {
            xp += 150
        }
        if (completedDailyMission) {
            xp += 50
        }
        return xp
    }

    fun calculateLevel(totalXP: Int): Int {
        return 1 + (totalXP / 1000)
    }

    fun getLevelTitle(level: Int): String {
        return when {
            level <= 1 -> "Nível 1"
            level == 2 -> "Nível 2"
            level == 3 -> "Nível 3"
            level in 4..6 -> "Especialista"
            level in 7..9 -> "Master"
            else -> "Legend"
        }
    }

    fun calculateTaskCompletionRate(completedTasks: Int, totalTasks: Int): Float {
        return if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0.8f
    }

    fun calculateHabitCompletionRate(completedHabits: Int, totalHabits: Int): Float {
        return if (totalHabits > 0) completedHabits.toFloat() / totalHabits else 0.7f
    }

    fun calculateAverageRate(taskRate: Float, habitRate: Float): Float {
        return (taskRate + habitRate) / 2f
    }

    fun calculateWeeklyStats(
        completedTasks: Int,
        completedHabits: Int,
        totalTasks: Int,
        totalHabits: Int,
        averageRate: Float
    ): WeeklyStats {
        return WeeklyStats(
            rates = mapOf(
                "Seg" to 0.8f,
                "Ter" to 0.7f,
                "Qua" to 0.9f,
                "Qui" to 0.6f,
                "Sex" to averageRate,
                "Sáb" to 0.5f,
                "Dom" to 0.4f
            ),
            completedCount = completedTasks + completedHabits,
            totalCount = totalTasks + totalHabits
        )
    }

    fun calculateFinanceSummary(transactions: List<Transaction>): FinanceSummary {
        val totalIncome = transactions.filter { it.isIncome }.sumOf { it.amount }
        val totalExpense = transactions.filter { !it.isIncome }.sumOf { it.amount }
        val balance = totalIncome - totalExpense
        return FinanceSummary(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            balance = balance,
            recentTransactions = transactions.take(5)
        )
    }
}
