package com.lifeos.core_ai.analytics

class CostGuard(
    private val maxRequests: Int,
    private val maxTokens: Int
) {

    fun canCallAI(
        usage: AIUsageTracker
    ): Boolean {
        if (usage.requestsToday >= maxRequests)
            return false

        if (usage.tokensToday >= maxTokens)
            return false

        return true
    }

}
