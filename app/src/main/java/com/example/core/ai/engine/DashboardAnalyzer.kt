package com.example.core.ai.engine

import com.example.core.ai.factory.AIInsightFactory
import com.example.core.ai.model.*

class DashboardAnalyzer {

    fun analyze(
        dashboard: DashboardData
    ): List<AIInsight> {

        val list = mutableListOf<AIInsight>()

        if (dashboard.overdueTasks > 0)
            list.add(
                AIInsightFactory.overdueTask(
                    dashboard.overdueTasks
                )
            )

        if (dashboard.waterPercentage < 60)
            list.add(
                AIInsightFactory.lowWater()
            )

        if (dashboard.inactiveProjects > 0)
            list.add(
                AIInsightFactory.inactiveProject()
            )

        return list

    }

}
