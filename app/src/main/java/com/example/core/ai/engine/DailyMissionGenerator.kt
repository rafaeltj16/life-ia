package com.example.core.ai.engine

import com.example.core.ai.model.*

class DailyMissionGenerator {

    fun generate(
        insights: List<AIInsight>
    ): DailyMission? {

        val priority =
            insights.maxByOrNull { it.priority.ordinal }

        return priority?.let {

            DailyMission(

                title = it.title,

                description = it.message,

                estimatedMinutes = 60,

                priority = it.priority,

                action = it.action

            )

        }

    }

}
