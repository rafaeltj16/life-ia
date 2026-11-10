package com.example.core.ai.life_ai

import android.content.Context
import com.example.core.ai.model.UserContext
import com.example.core.ai.repository.AIPlanItem
import com.example.core.ai.repository.AIRepository
import com.example.features.ai.data.repository.AIRepositoryImpl
import com.example.features.ai.domain.usecase.GenerateDailyPlanUseCase

class PlannerEngine(context: Context) {
    private val repository: AIRepository = AIRepositoryImpl(context)
    private val useCase = GenerateDailyPlanUseCase(repository)

    suspend fun generatePlan(userContext: UserContext): List<AIPlanItem> {
        return useCase(userContext)
    }

    suspend fun generatePlanDirect(
        memories: List<com.example.core.ai.repository.AIMemoryItem>,
        habits: List<String>,
        tasks: List<String>
    ): List<AIPlanItem> {
        return repository.generateDailyPlan(memories, habits, tasks)
    }
}
