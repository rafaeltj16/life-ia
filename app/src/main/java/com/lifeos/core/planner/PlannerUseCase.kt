package com.lifeos.core.planner

import kotlinx.coroutines.flow.Flow

class PlannerUseCase(
    val repository: PlannerRepository = PlannerRepositoryImpl(),
    val engine: PlannerEngine = PlannerEngine()
) {
    fun getPlannerState(): Flow<PlannerState> {
        return repository.getPlannerState()
    }

    suspend fun reoptimizePlan() {
        val newPlan = engine.generateOptimizedPlan()
        repository.updatePlannerState(newPlan)
    }
}
