package com.example.features.copilot.domain

import com.example.features.copilot.data.CopilotRepository
import com.example.features.copilot.data.GoalPlan
import kotlinx.coroutines.flow.Flow

class CopilotPlanner(
    private val repository: CopilotRepository
) {
    fun getActivePlan(): Flow<GoalPlan?> = repository.getActiveGoalPlan()

    suspend fun generatePlanFromPrompt(prompt: String): GoalPlan {
        return repository.createGoalPlan(prompt)
    }
}
