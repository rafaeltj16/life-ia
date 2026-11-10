package com.example.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Standard categories supported across the task management system.
 * Contains localized display names, English keys, and themed color codes.
 */
enum class TaskCategory(
    val displayName: String,
    val key: String,
    val colorHex: Long
) {
    PERSONAL("Pessoal", "Personal", 0xFF8E24AA),
    WORK("Trabalho", "Work", 0xFF1E88E5),
    HEALTH("Saúde", "Health", 0xFFE53935),
    STUDIES("Estudos", "Studies", 0xFF00897B),
    FINANCE("Finanças", "Finance", 0xFFFB8C00),
    GENERAL("Geral", "General", 0xFF546E7A);

    companion object {
        fun fromString(value: String?): TaskCategory {
            if (value.isNullOrBlank()) return GENERAL
            return entries.firstOrNull {
                it.name.equals(value, ignoreCase = true) ||
                it.displayName.equals(value, ignoreCase = true) ||
                it.key.equals(value, ignoreCase = true)
            } ?: GENERAL
        }
    }
}

/**
 * Task entity in Room representing a task in the local database.
 * Supports a comprehensive task management system with fields for title, description,
 * priority, due date, category, status, and completion state.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val priority: String = "MEDIUM", // LOW, MEDIUM, HIGH, URGENT
    val dueDate: String = "",        // formatted date, e.g. "Hoje", "Amanhã", "2026-11-08"
    val category: String = "Pessoal",
    val isCompleted: Boolean = false,
    val status: String = "TO_DO",    // TO_DO, IN_PROGRESS, WAITING, COMPLETED, ARCHIVED
    val tags: String = "",           // Comma-separated tags
    val isFavorite: Boolean = false,
    val projectId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    val taskCategory: TaskCategory
        get() = TaskCategory.fromString(category)
}
