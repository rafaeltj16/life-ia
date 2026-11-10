package com.example.core.ai.memory

class MemoryUpdater(private val repository: MemoryRepository) {
    /**
     * Updates or reinforces a memory based on user feedback or action.
     */
    fun reinforceMemory(fact: String, category: String, confidenceBoost: Float = 0.1f) {
        val memories = repository.getMemories()
        val existing = memories.find { it.fact.lowercase() == fact.lowercase() && it.category == category }
        
        if (existing != null) {
            val newConfidence = (existing.confidence + confidenceBoost).coerceAtMost(1.0f)
            val updated = existing.copy(
                strength = existing.strength + 1,
                confidence = newConfidence,
                lastUpdated = System.currentTimeMillis()
            )
            repository.saveMemory(updated)
        } else {
            val newMemory = UserMemory(
                fact = fact,
                category = category,
                confidence = (0.5f + confidenceBoost).coerceIn(0.0f, 1.0f)
            )
            repository.saveMemory(newMemory)
        }
    }

    /**
     * Lowers confidence of a memory if user explicitly rejects or corrects it.
     */
    fun decayMemory(id: String, decayAmount: Float = 0.2f) {
        val memories = repository.getMemories()
        val existing = memories.find { it.id == id } ?: return
        
        val newConfidence = existing.confidence - decayAmount
        if (newConfidence <= 0.1f) {
            // Delete if confidence drops too low
            repository.deleteMemory(id)
        } else {
            val updated = existing.copy(
                confidence = newConfidence,
                lastUpdated = System.currentTimeMillis()
            )
            repository.saveMemory(updated)
        }
    }
}
