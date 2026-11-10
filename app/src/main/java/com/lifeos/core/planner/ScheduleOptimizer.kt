package com.lifeos.core.planner

class ScheduleOptimizer(
    val priorityCalculator: PriorityCalculator = PriorityCalculator(),
    val timeEstimator: TimeEstimator = TimeEstimator(),
    val conflictResolver: ConflictResolver = ConflictResolver()
) {
    fun optimizeSchedule(inputSlots: List<PlannedTimeSlot>): List<PlannedTimeSlot> {
        val sortedSlots = inputSlots.sortedByDescending { it.priorityScore }
        return conflictResolver.resolveConflicts(sortedSlots)
    }
}
