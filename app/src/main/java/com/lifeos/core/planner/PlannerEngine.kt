package com.lifeos.core.planner

class PlannerEngine(
    val scheduleOptimizer: ScheduleOptimizer = ScheduleOptimizer(),
    val priorityCalculator: PriorityCalculator = PriorityCalculator(),
    val timeEstimator: TimeEstimator = TimeEstimator()
) {
    fun generateOptimizedPlan(): PlannerState {
        val rawSlots = listOf(
            PlannedTimeSlot("p1", "Sprint de Foco em Kotlin", "09:00", 45, isFocusBlock = true, priorityScore = 0.95f),
            PlannedTimeSlot("p2", "Alinhamento com Equipe", "11:00", 30, isFocusBlock = false, priorityScore = 0.80f),
            PlannedTimeSlot("p3", "Desenvolvimento Dashboard", "15:00", 60, isFocusBlock = true, priorityScore = 0.90f)
        )

        val optimized = scheduleOptimizer.optimizeSchedule(rawSlots)
        return PlannerState(
            dailyPlan = optimized,
            availableMinutes = 105,
            occupiedMinutes = 105,
            isOptimized = true
        )
    }
}
