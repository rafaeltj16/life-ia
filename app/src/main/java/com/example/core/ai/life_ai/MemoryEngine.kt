package com.example.core.ai.life_ai

import android.content.Context
import com.example.core.ai.memory.*

class MemoryEngine(
    private val repository: MemoryRepository
) {
    constructor(context: Context) : this(SharedPreferencesMemoryRepository(context))

    private val updater = if (repository is SharedPreferencesMemoryRepository) {
        MemoryUpdater(repository)
    } else {
        null
    }

    suspend fun register(
        event: UserEvent
    ) {
        repository.saveEvent(event)
        repository.updateProfile()
    }

    suspend fun profile(): MemoryProfile {
        return repository.loadProfile()
    }

    fun getMemories(): List<UserMemory> {
        return if (repository is SharedPreferencesMemoryRepository) {
            repository.getMemories()
        } else {
            emptyList()
        }
    }

    fun saveMemory(memory: UserMemory) {
        if (repository is SharedPreferencesMemoryRepository) {
            repository.saveMemory(memory)
        }
    }

    fun deleteMemory(id: String) {
        if (repository is SharedPreferencesMemoryRepository) {
            repository.deleteMemory(id)
        }
    }

    fun reinforce(fact: String, category: String) {
        if (repository is SharedPreferencesMemoryRepository) {
            updater?.reinforceMemory(fact, category)
        }
    }

    fun decay(id: String) {
        if (repository is SharedPreferencesMemoryRepository) {
            updater?.decayMemory(id)
        }
    }

    fun clearAll() {
        repository.clearAll()
    }
}
