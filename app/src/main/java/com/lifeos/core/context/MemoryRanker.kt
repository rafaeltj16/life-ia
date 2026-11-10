package com.lifeos.core.context

import java.time.Duration
import java.time.Instant

class MemoryRanker {
    fun rankMemories(memories: List<ConversationMemory>, query: String): List<ConversationMemory> {
        return memories.map { memory ->
            var score = 0.5f
            if (query.isNotBlank() && (memory.question.contains(query, ignoreCase = true) || memory.answer.contains(query, ignoreCase = true))) {
                score += 0.4f
            }
            if (Duration.between(memory.createdAt, Instant.now()).toHours() < 24) {
                score += 0.1f
            }
            memory.copy(relevance = score.coerceIn(0.0f, 1.0f))
        }.sortedByDescending { it.relevance }
    }
}

