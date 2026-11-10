package com.example.core.ai.model

enum class InsightPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

val InsightPriority.score: Int
    get() = when (this) {
        InsightPriority.LOW -> 20
        InsightPriority.MEDIUM -> 50
        InsightPriority.HIGH -> 80
        InsightPriority.CRITICAL -> 100
    }

