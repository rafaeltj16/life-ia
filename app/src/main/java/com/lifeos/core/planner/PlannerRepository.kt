package com.lifeos.core.planner

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface PlannerRepository {
    fun getPlannerState(): Flow<PlannerState>
    suspend fun updatePlannerState(state: PlannerState)
    suspend fun addPlannedSlot(slot: PlannedTimeSlot)
}

class PlannerRepositoryImpl : PlannerRepository {
    private val _state = MutableStateFlow(
        PlannerState(
            dailyPlan = listOf(
                PlannedTimeSlot("p1", "Sprint de Foco Kotlin", "09:00", 45, isFocusBlock = true, priorityScore = 0.95f),
                PlannedTimeSlot("p2", "Revisão do Wireframe", "11:00", 30, isFocusBlock = false, priorityScore = 0.80f),
                PlannedTimeSlot("p3", "Bloco Pomodoro LifeOS AI", "15:00", 50, isFocusBlock = true, priorityScore = 0.90f)
            ),
            availableMinutes = 105,
            occupiedMinutes = 125,
            isOptimized = true
        )
    )

    override fun getPlannerState(): Flow<PlannerState> = _state.asStateFlow()

    override suspend fun updatePlannerState(state: PlannerState) {
        _state.value = state
    }

    override suspend fun addPlannedSlot(slot: PlannedTimeSlot) {
        _state.value = _state.value.copy(dailyPlan = _state.value.dailyPlan + slot)
    }
}
