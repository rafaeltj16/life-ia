package com.lifeos.core_ai.learning

data class UserBehaviorProfile(
    val preferredFocusHour: Int = 9,
    val averageTaskDuration: Int = 45,
    val notificationOpenRate: Float = 0.0f,
    val habitCompletionRate: Float = 0.0f,
    val productivityScore: Int = 0
)
