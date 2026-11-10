package com.lifeos.core_ai.analytics

data class AIUsageTracker(
    val requestsToday: Int,
    val tokensToday: Int,
    val averageLatency: Long,
    val cacheHits: Int
)
