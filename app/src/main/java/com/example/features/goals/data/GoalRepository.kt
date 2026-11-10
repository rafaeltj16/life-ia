package com.example.features.goals.data

import com.example.features.goals.domain.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun getGoals(): Flow<List<Goal>>
    suspend fun getGoalById(id: String): Goal?
    suspend fun insertGoal(goal: Goal)
    suspend fun updateProgress(id: String, progress: Int)
    suspend fun deleteGoal(id: String)
}
