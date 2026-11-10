package com.example.core.ai.score

class FocusScoreCalculator {

    fun calculate(
        completedTasks: Int,
        interruptions: Int
    ): Int {

        if (completedTasks == 0)
            return 0

        val score =
            completedTasks * 20 - interruptions * 5

        return score.coerceIn(0, 100)

    }

}
