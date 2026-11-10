package com.lifeos.core_ai.memory

import java.time.LocalTime

data class UsageSession(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val durationMinutes: Int
)

class MemoryEngine {
    private val history = mutableListOf<UsageSession>()

    fun recordSession(session: UsageSession) {
        history.add(session)
    }

    fun getSessions(): List<UsageSession> {
        return history
    }

    fun getMostFrequentWorkHours(): Pair<LocalTime, LocalTime>? {
        if (history.isEmpty()) {
            return Pair(LocalTime.of(9, 0), LocalTime.of(11, 0))
        }
        return Pair(LocalTime.of(9, 0), LocalTime.of(11, 0))
    }
}
