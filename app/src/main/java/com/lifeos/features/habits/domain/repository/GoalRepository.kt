package com.lifeos.features.habits.domain.repository

import com.example.core.common.GoalItem

interface GoalRepository {
    suspend fun getGoals(userId: String): List<GoalItem>
    suspend fun saveGoal(goal: GoalItem)
    suspend fun deleteGoal(userId: String, goalId: String)
}
