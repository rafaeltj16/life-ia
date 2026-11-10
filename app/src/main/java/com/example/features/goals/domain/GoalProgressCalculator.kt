package com.example.features.goals.domain

class GoalProgressCalculator {

    fun calculate(goal: Goal): Float {
        val totalTasks = goal.tasks.size
        val completedTasks = goal.tasks.count { it.completed }

        val totalHabits = goal.habits.size
        val completedHabits = goal.habits.count { it.completedToday }

        val totalProjects = goal.projects.size
        val completedProjects = goal.projects.count { it.completed }

        val total = totalTasks + totalHabits + totalProjects

        if (total == 0) return goal.progress

        val completed = completedTasks + completedHabits + completedProjects

        return completed.toFloat() / total.toFloat()
    }

    fun calculatePercentage(currentProgress: Int, targetProgress: Int): Int {
        if (targetProgress <= 0) return 0
        return ((currentProgress.toFloat() / targetProgress.toFloat()) * 100)
            .toInt()
            .coerceIn(0, 100)
    }

    fun isCompleted(currentProgress: Int, targetProgress: Int): Boolean {
        return currentProgress >= targetProgress
    }

    fun calculateOverallProgress(goals: List<Goal>): Int {
        if (goals.isEmpty()) return 0
        val sum = goals.sumOf { calculatePercentage(it.currentProgress, it.targetProgress) }
        return sum / goals.size
    }
}
