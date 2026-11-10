package com.lifeos.core.database.entity

import androidx.room.*

@Entity(tableName = "life_tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: String = "MEDIUM",
    val dueDate: Long? = null,
    val category: String = "GENERAL",
    val projectId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "life_habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String = "",
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val targetDays: Int = 7,
    val completedToday: Boolean = false,
    val category: String = "HEALTH",
    val iconName: String = "star",
    val colorHex: String = "#3B82F6",
    val frequency: String = "DAILY",
    val targetValue: Int = 1,
    val unit: String = "vez",
    val reminderTime: String? = null,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "life_habit_logs",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habitId"]),
        Index(value = ["completionDate"]),
        Index(value = ["habitId", "completionDate"], unique = true)
    ]
)
data class HabitLogEntity(
    @PrimaryKey val id: String,
    val habitId: String,
    val completionDate: String, // Format: YYYY-MM-DD
    val completed: Boolean = true,
    val valueLogged: Int = 1,
    val notes: String = "",
    val loggedAt: Long = System.currentTimeMillis()
)

data class HabitWithLogs(
    @Embedded val habit: HabitEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "habitId"
    )
    val logs: List<HabitLogEntity> = emptyList()
)

@Entity(tableName = "life_projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String = "",
    val progress: Float = 0f,
    val status: String = "ACTIVE",
    val deadline: Long? = null,
    val colorHex: String = "#3B82F6"
)

@Entity(tableName = "life_goals")
data class GoalEntity(
    @PrimaryKey val id: String,
    val title: String,
    val targetValue: Float = 100f,
    val currentValue: Float = 0f,
    val category: String = "PERSONAL",
    val deadline: Long? = null,
    val isAchieved: Boolean = false
)

@Entity(tableName = "life_focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey val id: String,
    val taskTitle: String,
    val durationMinutes: Int,
    val completedAt: Long = System.currentTimeMillis(),
    val productivityRating: Int = 5
)

@Entity(tableName = "life_finance_records")
data class FinanceEntity(
    @PrimaryKey val id: String,
    val description: String,
    val amount: Double,
    val type: String, // INCOME, EXPENSE
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "life_health_records")
data class HealthEntity(
    @PrimaryKey val id: String,
    val metricType: String, // SLEEP, STEPS, WATER, WORKOUT
    val value: Float,
    val unit: String,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "life_knowledge_notes")
data class KnowledgeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val tags: String = "", // Comma separated
    val category: String = "SECOND_BRAIN",
    val updatedAt: Long = System.currentTimeMillis()
)
