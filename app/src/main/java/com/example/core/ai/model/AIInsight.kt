package com.example.core.ai.model

import java.time.Instant

data class AIInsight(
    val id: String,
    val title: String,
    val message: String,
    val type: AIInsightType,
    val priority: InsightPriority,
    val action: AIAction? = null,
    val createdAt: Instant = Instant.now(),
    val dismissed: Boolean = false
) {
    // Secondary constructor to support existing instantiations passing Long
    constructor(
        id: String,
        title: String,
        message: String,
        type: AIInsightType,
        priority: InsightPriority,
        action: AIAction?,
        createdAt: Long
    ) : this(
        id = id,
        title = title,
        message = message,
        type = type,
        priority = priority,
        action = action,
        createdAt = Instant.ofEpochMilli(createdAt),
        dismissed = false
    )
}
