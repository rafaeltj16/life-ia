package com.example.core.ai.memory

import java.util.UUID

data class UserMemory(
    val id: String = UUID.randomUUID().toString(),
    val fact: String,
    val category: String, // e.g., "Saúde", "Trabalho", "Financeiro", "Hábito", "Geral"
    val strength: Int = 1, // How many times this pattern/fact was detected or reinforced (1-10)
    val confidence: Float = 1.0f, // Confidence score (0.0 to 1.0)
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)
