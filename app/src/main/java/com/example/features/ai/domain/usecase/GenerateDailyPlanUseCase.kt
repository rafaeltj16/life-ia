package com.example.features.ai.domain.usecase

import com.example.core.ai.model.UserContext
import com.example.core.ai.repository.AIPlanItem
import com.example.core.ai.repository.AIRepository

class GenerateDailyPlanUseCase(private val repository: AIRepository) {
    suspend operator fun invoke(userContext: UserContext): List<AIPlanItem> {
        val habits = userContext.dashboardData.todayHabits.map { it.name }
        val tasks = userContext.dashboardData.todayTasks.map { it.title }
        return repository.generateDailyPlan(
            memories = userContext.memories,
            habits = habits,
            tasks = tasks
        )
    }
}
