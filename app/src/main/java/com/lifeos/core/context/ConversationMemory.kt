package com.lifeos.core.context

import java.time.Instant

data class ConversationMemory(
    val id: String,
    val question: String,
    val answer: String,
    val createdAt: Instant = Instant.now(),
    val relevance: Float = 0.5f
)

