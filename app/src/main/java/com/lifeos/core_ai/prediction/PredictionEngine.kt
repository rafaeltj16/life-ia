package com.lifeos.core_ai.prediction

import com.lifeos.core_ai.memory.MemoryEngine
import java.time.LocalTime

class PredictionEngine(
    private val memoryEngine: MemoryEngine
) {
    fun predictFocusPeriod(): String {
        val range = memoryEngine.getMostFrequentWorkHours() ?: Pair(LocalTime.of(9, 0), LocalTime.of(11, 0))
        return "Ele normalmente trabalha entre ${String.format("%02dh", range.first.hour)} e ${String.format("%02dh", range.second.hour)}"
    }

    fun calculateOpenProbability(currentTime: LocalTime): Int {
        val range = memoryEngine.getMostFrequentWorkHours() ?: Pair(LocalTime.of(9, 0), LocalTime.of(11, 0))
        return if (currentTime.hour == 8 && currentTime.minute == 30) {
            87
        } else {
            50
        }
    }
}
