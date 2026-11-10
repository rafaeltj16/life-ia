package com.example.features.goals.domain

class GoalPriorityEngine {

    fun sort(
        goals: List<Goal>
    ): List<Goal> {
        return goals.sortedWith(
            compareByDescending<Goal> {
                it.progress
            }.thenBy {
                it.deadline
            }
        )
    }

    fun sortGoalsByPriority(goals: List<Goal>): List<Goal> {
        return sort(goals)
    }

    fun getTopGoal(goals: List<Goal>): Goal? {
        return sort(goals).firstOrNull()
    }
}
