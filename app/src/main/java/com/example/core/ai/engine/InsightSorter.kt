package com.example.core.ai.engine

import com.example.core.ai.model.AIInsight
import com.example.core.ai.model.InsightPriority
import com.example.core.ai.model.score

object InsightSorter {
    /**
     * Sorts insights by priority (HIGH -> MEDIUM -> LOW) and then by creation time descending.
     */
    fun sort(insights: List<AIInsight>): List<AIInsight> {
        return insights.sortedWith(
            compareByDescending<AIInsight> { it.priority.score }
                .thenByDescending { it.createdAt }
        )
    }
}
