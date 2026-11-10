package com.example.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subtasks")
data class SubTaskEntity(
    @PrimaryKey val id: String,
    val taskId: String,
    val title: String,
    val completed: Boolean
)
