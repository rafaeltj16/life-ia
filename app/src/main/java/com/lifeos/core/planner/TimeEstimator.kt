package com.lifeos.core.planner

data class EstimationCalibration(
    val taskName: String,
    val initialEstimateMinutes: Int,
    val historicalAverageMinutes: Int,
    val calibratedEstimateMinutes: Int
)

class TimeEstimator {
    fun estimateDurationMinutes(taskType: String, complexity: Int = 1): Int {
        val baseMinutes = when (taskType.uppercase()) {
            "CODING", "DESENVOLVIMENTO", "KOTLIN" -> 45
            "DESIGN", "WIREFRAME" -> 30
            "WRITING", "NOTA", "ESTUDO" -> 20
            "MEETING", "REUNIÃO" -> 30
            else -> 25
        }
        return baseMinutes * complexity.coerceIn(1, 3)
    }

    fun calibrateEstimate(
        taskName: String,
        initialEstimateMinutes: Int,
        historicalAverageMinutes: Int
    ): EstimationCalibration {
        // Recalibration formula balancing original prediction with user history
        val calibrated = (initialEstimateMinutes * 0.10 + historicalAverageMinutes * 0.90).toInt()
        return EstimationCalibration(
            taskName = taskName,
            initialEstimateMinutes = initialEstimateMinutes,
            historicalAverageMinutes = historicalAverageMinutes,
            calibratedEstimateMinutes = calibrated
        )
    }
}

