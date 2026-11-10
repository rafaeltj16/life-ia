package com.lifeos.core.prediction

class DelayPredictor {

    fun predictProjectDelay(
        projectName: String,
        pendingTasks: Int,
        completedTasks: Int,
        daysRemaining: Int
    ): Int {
        val total = pendingTasks + completedTasks
        if (total == 0) return 0

        val completionRate = completedTasks.toFloat() / total.toFloat()
        val estimatedDaysNeeded = if (completionRate > 0) (pendingTasks / (completedTasks.toFloat() / 7f)).toInt() else pendingTasks * 2

        val delay = estimatedDaysNeeded - daysRemaining
        return if (delay > 0) delay else 0
    }
}
