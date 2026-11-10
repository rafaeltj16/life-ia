package com.example.core.common

import java.util.UUID

data class Achievement(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val icon: String = "🏆",
    val xpReward: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: String? = null
)
