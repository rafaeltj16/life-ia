package com.lifeos.core.prediction

class PatternDetector {

    fun detectPeakProductivityHours(focusHours: List<Int>): Int {
        if (focusHours.isEmpty()) return 10
        return focusHours.groupBy { it }
            .maxByOrNull { it.value.size }?.key ?: 10
    }

    fun detectProcrastinationTriggers(completedLateTaskCount: Int): List<String> {
        val triggers = mutableListOf<String>()
        if (completedLateTaskCount > 3) {
            triggers.add("Excesso de tarefas sem blocos de foco agendados")
            triggers.add("Conflitos de horário nas quartas e quintas-feiras")
        }
        return triggers
    }
}
