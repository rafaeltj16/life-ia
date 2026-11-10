package com.example.features.dashboard

import com.example.core.common.DashboardMetricsCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class DashboardMetricsCalculatorTest {

    @Test
    fun testCalculateXP() {
        // Base XP is 2500, each task is 25 XP, each habit is 10 XP
        val xp = DashboardMetricsCalculator.calculateXP(completedTasksCount = 3, completedHabitsCount = 2)
        assertEquals(2500 + (3 * 25) + (2 * 10), xp)
    }

    @Test
    fun testCalculateLevel() {
        // Level is 1 + (totalXP / 1000)
        val level = DashboardMetricsCalculator.calculateLevel(3500)
        assertEquals(4, level)
    }

    @Test
    fun testTaskCompletionRate() {
        val rate = DashboardMetricsCalculator.calculateTaskCompletionRate(completedTasks = 2, totalTasks = 5)
        assertEquals(0.4f, rate, 0.001f)
    }

    @Test
    fun testHabitCompletionRate() {
        val rate = DashboardMetricsCalculator.calculateHabitCompletionRate(completedHabits = 0, totalHabits = 0)
        assertEquals(0.7f, rate, 0.001f)
    }
}
