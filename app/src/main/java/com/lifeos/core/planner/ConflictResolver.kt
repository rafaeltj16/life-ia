package com.lifeos.core.planner

class ConflictResolver {
    fun resolveConflicts(slots: List<PlannedTimeSlot>): List<PlannedTimeSlot> {
        val resolved = slots.toMutableList()
        for (i in 0 until resolved.size - 1) {
            val current = resolved[i]
            val next = resolved[i + 1]
            if (current.startTime == next.startTime) {
                resolved[i + 1] = next.copy(isConflict = false)
            }
        }
        return resolved
    }
}
