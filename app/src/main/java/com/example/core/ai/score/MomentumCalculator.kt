package com.example.core.ai.score

class MomentumCalculator {

    fun calculate(
        today: Int,
        yesterday: Int
    ): Int {
        return today - yesterday
    }

}
